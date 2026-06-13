package com.bus.seat.booking.service;

import com.bus.seat.booking.configuration.DataInitializer;
import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.model.BookingStatus;
import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.City;
import com.bus.seat.booking.model.CustomerTrip;
import com.bus.seat.booking.model.Journey;
import com.bus.seat.booking.model.SeatAvailabilityStatus;
import com.bus.seat.booking.model.SeatBooking;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class CheckAvailabilityServiceTest {

    private CheckAvailabilityService checkAvailabilityService;

    @BeforeEach
    void beforeEach() {
        DataInitializer.BOOKED_SEATS.clear();
        checkAvailabilityService = new CheckAvailabilityService();
    }

    @Test
    void testWhenSeatBookedForOnePassengerAtoBThenCheckAvailabilityShouldReturnFullAvailable() {

        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "B", 1, "customer1");

        Assertions.assertEquals(SeatAvailabilityStatus.FULLY_AVAILABLE, response.isSeatsAvailable());
        Assertions.assertTrue(response.getAvailableSeats().containsKey("1A"));
    }

    @Test
    void testWhenSeatBookedForThreePassengerAtoBThenCheckAvailabilityShouldBookThreeSeats() {

        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "B", 3, "customer1");

        Assertions.assertEquals(3, response.getAvailableSeats().size());
        Assertions.assertEquals(150.0, response.getTotalPrice());
    }

    @Test
    void testWhenBookingAvailableForTheSeatThatDoNotOverlapThenCheckAvailabilityShouldBook1A() {

        // Add Booking for 1A seat. Origin A. Destination C
        final Journey journey = new Journey();
        journey.setBusTrip(BusTrip.FIRST_TRIP);
        journey.setOrigin(City.A);
        journey.setDestination(City.C);
        journey.setBookedCities(Arrays.asList(City.A, City.B));
        journey.setCustomerTrip(CustomerTrip.AC);

        final SeatBooking seatBooking =
                new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                        BookingStatus.PENDING, journey);

        final List<SeatBooking> seatBookingList = new ArrayList<>();
        seatBookingList.add(seatBooking);

        final ConcurrentMap<BusTrip, List<SeatBooking>> seatBookingMap = new ConcurrentHashMap<>();
        seatBookingMap.put(BusTrip.FIRST_TRIP, seatBookingList);

        DataInitializer.BOOKED_SEATS.put("1A", seatBookingMap);

        // Check availability of the seat. origin C. Destination D
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "C", "D", 1, "customer1");

        Assertions.assertEquals(1, response.getAvailableSeats().size());
        Assertions.assertTrue(response.getAvailableSeats().containsKey("1A"));
    }

    @Test
    void testWhenBookingAvailableForTheSeatThatOverlapThenCheckAvailabilityShouldBook1B() {

        // Add Booking for 1A seat. Origin A. Destination C
        final Journey journey = new Journey();
        journey.setBusTrip(BusTrip.FIRST_TRIP);
        journey.setOrigin(City.A);
        journey.setDestination(City.C);
        journey.setBookedCities(Arrays.asList(City.A, City.B));
        journey.setCustomerTrip(CustomerTrip.AC);

        final SeatBooking seatBooking =
                new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                        BookingStatus.PENDING, journey);

        final List<SeatBooking> seatBookingList = new ArrayList<>();
        seatBookingList.add(seatBooking);

        final ConcurrentMap<BusTrip, List<SeatBooking>> seatBookingMap = new ConcurrentHashMap<>();
        seatBookingMap.put(BusTrip.FIRST_TRIP, seatBookingList);

        DataInitializer.BOOKED_SEATS.put("1A", seatBookingMap);

        // Check availability of the seat. origin A. Destination B
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "B", 1, "customer1");

        // Check availability add booking for 1B seat since there is a booking in 1A seat from A to C
        Assertions.assertEquals(1, response.getAvailableSeats().size());
        Assertions.assertTrue(response.getAvailableSeats().containsKey("1B"));
    }

    @Test
    void testWhenBookingAvailableForTheSeatThatOverlapAndExpiredThenCheckAvailabilityShouldBookSameSeat() {

        // Add Booking for 1A seat. Origin A. Destination C
        final Journey journey = new Journey();
        journey.setBusTrip(BusTrip.FIRST_TRIP);
        journey.setOrigin(City.A);
        journey.setDestination(City.C);
        journey.setBookedCities(Arrays.asList(City.A, City.B));
        journey.setCustomerTrip(CustomerTrip.AC);

        final SeatBooking seatBooking =
                new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                        BookingStatus.PENDING, journey);
        seatBooking.setCreatedDateTime(Instant.now().minusSeconds(180));

        final List<SeatBooking> seatBookingList = new ArrayList<>();
        seatBookingList.add(seatBooking);

        final ConcurrentMap<BusTrip, List<SeatBooking>> seatBookingMap = new ConcurrentHashMap<>();
        seatBookingMap.put(BusTrip.FIRST_TRIP, seatBookingList);

        DataInitializer.BOOKED_SEATS.put("1A", seatBookingMap);

        // Check availability of the seat. origin A. Destination C
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 1, "customer1");

        // Check availability add booking for 1A seat since previous booking is expired without confirmation
        Assertions.assertEquals(1, response.getAvailableSeats().size());
        Assertions.assertTrue(response.getAvailableSeats().containsKey("1A"));
    }

    @Test
    void testWhenBookSeatForReturnTripSeatIsFullForFirstTripThenCheckAvailabilityShouldBook1A() {

        // Add Booking for 1A seat. Origin A. Destination D
        final Journey journey = new Journey();
        journey.setBusTrip(BusTrip.FIRST_TRIP);
        journey.setOrigin(City.A);
        journey.setDestination(City.D);
        journey.setBookedCities(Arrays.asList(City.A, City.B, City.C));
        journey.setCustomerTrip(CustomerTrip.AD);

        final SeatBooking seatBooking =
                new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                        BookingStatus.PENDING, journey);

        final List<SeatBooking> seatBookingList = new ArrayList<>();
        seatBookingList.add(seatBooking);

        final ConcurrentMap<BusTrip, List<SeatBooking>> seatBookingMap = new ConcurrentHashMap<>();
        seatBookingMap.put(BusTrip.FIRST_TRIP, seatBookingList);

        DataInitializer.BOOKED_SEATS.put("1A", seatBookingMap);

        // Check availability of the seat. origin D. Destination C. Return Trip
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "D", "C", 1, "customer1");

        // Check availability add booking for 1A seat since it is fully available for return trip
        Assertions.assertEquals(1, response.getAvailableSeats().size());
        Assertions.assertTrue(response.getAvailableSeats().containsKey("1A"));
        Assertions.assertEquals(BusTrip.RETURN_TRIP, response.getBusTrip());
    }

    @Test
    void testWhenBookFortySeatsWhenBookingAreAvailableThenCheckAvailabilityShouldReturnPartiallyAvailable() {

        // Add Booking for 1A seat. Origin A. Destination D
        final Journey journey = new Journey();
        journey.setBusTrip(BusTrip.FIRST_TRIP);
        journey.setOrigin(City.A);
        journey.setDestination(City.D);
        journey.setBookedCities(Arrays.asList(City.A, City.B, City.C));
        journey.setCustomerTrip(CustomerTrip.AD);

        final SeatBooking seatBooking =
                new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                        BookingStatus.PENDING, journey);

        final List<SeatBooking> seatBookingList = new ArrayList<>();
        seatBookingList.add(seatBooking);

        final ConcurrentMap<BusTrip, List<SeatBooking>> seatBookingMap = new ConcurrentHashMap<>();
        seatBookingMap.put(BusTrip.FIRST_TRIP, seatBookingList);

        DataInitializer.BOOKED_SEATS.put("1A", seatBookingMap);

        // Check availability of the seat. origin A. Destination C
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 40, "customer1");

        // Check availability returns Partially available status since it can not book all 40 seats
        Assertions.assertEquals(SeatAvailabilityStatus.PARTIALLY_AVAILABLE, response.isSeatsAvailable());
        Assertions.assertEquals(39, response.getAvailableSeats().size());
        Assertions.assertEquals(3900.0, response.getTotalPrice());
    }

    @Test
    void testWhenTryBookSeatsWhenBookingAreFullForTheTripThenCheckAvailabilityShouldReturnNotAvailable() {

        // Book all forty seats. origin A. Destination C
        checkAvailabilityService.checkSeatAvailability(
                "A", "C", 40, "customer1");

        // Try booking same for 1 passenger
        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 1, "customer2");

        // Check availability returns Not available status since all seats are reserved temporally
        Assertions.assertEquals(SeatAvailabilityStatus.NOT_AVAILABLE, response.isSeatsAvailable());
    }
}