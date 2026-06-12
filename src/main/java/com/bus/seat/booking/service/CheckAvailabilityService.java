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
import com.bus.seat.booking.util.TripUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bus.seat.booking.configuration.DataInitializer.BOOKED_SEATS;

public class CheckAvailabilityService {

    private static final List<String> SEAT_COLUMNS = Arrays.asList("A", "B", "C", "D");

    /**
     * Check Availability of seats for the given trip and passenger count
     *
     * @param origin
     * @param destination
     * @param passengerCount
     * @param customerId
     * @return {@link CheckAvailabilityResponse}
     */
    public CheckAvailabilityResponse checkSeatAvailability(final String origin, final String destination,
    final int passengerCount, final String customerId) {

        final City originCity = TripUtils.determineCityFromCityString(origin);
        final City destinationCity = TripUtils.determineCityFromCityString(destination);

        final CustomerTrip customerTrip = TripUtils.determineCustomerTrip(originCity, destinationCity);
        final BusTrip busTrip = customerTrip.getBusTrip();

        final Journey journey = new Journey();
        journey.setJourneyId(UUID.randomUUID());
        journey.setOrigin(originCity);
        journey.setDestination(destinationCity);
        journey.setCustomerTrip(customerTrip);
        journey.setBusTrip(busTrip);
        journey.setBookedCities(customerTrip.getCitiesUnableToOnboard());
        journey.setEstimatedArrival(busTrip == BusTrip.FIRST_TRIP
                ? destinationCity.getFirstTripEstimatedArrivalTime()
                : destinationCity.getReturnTripEstimatedArrivalTime());

        final List<SeatBooking> availableSeats = new ArrayList<>();

        for (int seatRow = 1; seatRow <= 10; seatRow++) {

            for (final String seatColumn : SEAT_COLUMNS) {

                try {
                    DataInitializer.lock.writeLock().lock();

                    final String seatNumber = seatRow + seatColumn;

                    if (BOOKED_SEATS.containsKey(seatNumber)) {

                        final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap =
                                BOOKED_SEATS.get(seatNumber);

                        if (busTripBookingsMap.containsKey(busTrip)) {

                            this.checkSeatWhenBookingsAreAvailable(customerId, journey, busTrip, customerTrip,
                                    seatNumber, busTripBookingsMap, availableSeats);

                        } else {

                            this.checkSeatWhenSeatAvailableForTheWholeTrip(customerId, journey, busTrip, seatNumber,
                                    busTripBookingsMap, availableSeats);
                        }

                    } else {

                        this.checkSeatWhenWholeSeatIsAvailable(
                                customerId, journey, busTrip, seatNumber, availableSeats);

                    }

                } finally {
                    DataInitializer.lock.writeLock().unlock();
                }

                if (availableSeats.size() == passengerCount) {
                    break;
                }

            }

            if (availableSeats.size() == passengerCount) {
                break;
            }

        }

        final int availableSeatCount = availableSeats.size();

        final CheckAvailabilityResponse checkAvailabilityResponse = new CheckAvailabilityResponse();
        checkAvailabilityResponse.setOrigin(originCity.name());
        checkAvailabilityResponse.setDestination(destinationCity.name());

        if (availableSeatCount == passengerCount) {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.FULLY_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(passengerCount);
            checkAvailabilityResponse.setTotalPrice(customerTrip.getTripPrice() * passengerCount);
            checkAvailabilityResponse.setAvailableSeats(availableSeats);

        } else if (availableSeatCount < passengerCount) {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.PARTIALLY_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(availableSeatCount);
            checkAvailabilityResponse.setTotalPrice(customerTrip.getTripPrice() * availableSeatCount);
            checkAvailabilityResponse.setAvailableSeats(availableSeats);

        } else {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.NOT_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(0);
            checkAvailabilityResponse.setTotalPrice(0.00);
        }

        return checkAvailabilityResponse;
    }

    /**
     * Check seat when whole seat is available for two trips
     *
     * @param customerId
     * @param journey
     * @param busTrip
     * @param seatNumber
     * @param availableSeats
     */
    private void checkSeatWhenWholeSeatIsAvailable(final String customerId, final Journey journey,
    final BusTrip busTrip, final String seatNumber, final List<SeatBooking> availableSeats) {

        final SeatBooking seatBooking = this.createPendingSeatBooking(seatNumber, customerId, journey);

        final ConcurrentMap<BusTrip, List<SeatBooking>> busTripToBookingsMap = new ConcurrentHashMap<>();
        busTripToBookingsMap.put(busTrip, new ArrayList<>(Arrays.asList(seatBooking)));

        BOOKED_SEATS.put(seatNumber, busTripToBookingsMap);

        availableSeats.add(seatBooking);
    }

    /**
     * Check seat when there are bookings available for the seat
     *
     * @param customerId
     * @param journey
     * @param busTrip
     * @param customerTrip
     * @param seatNumber
     * @param busTripBookingsMap
     * @param availableSeats
     */
    private void checkSeatWhenBookingsAreAvailable(final String customerId, final Journey journey,
    final BusTrip busTrip, final CustomerTrip customerTrip, final String seatNumber,
    final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap, final List<SeatBooking> availableSeats) {

        final List<SeatBooking> seatBookingList = busTripBookingsMap.get(busTrip);

        final Set<City> unableToOnboardCitiesSet =
                new HashSet<>(customerTrip.getCitiesUnableToOnboard());

        final boolean bookingAvailable = seatBookingList.stream()
                .anyMatch(seatBooking ->
                        seatBooking.getJourney().getBookedCities().stream()
                                .noneMatch(unableToOnboardCitiesSet::contains));

        if (bookingAvailable) {

            final SeatBooking seatBooking = this.createPendingSeatBooking(
                    seatNumber, customerId, journey);

            seatBookingList.add(seatBooking);

            availableSeats.add(seatBooking);
        }
    }

    /**
     * Check the seat when the seat is available for the whole trip
     *
     * @param customerId
     * @param journey
     * @param busTrip
     * @param seatNumber
     * @param busTripBookingsMap
     * @param availableSeats
     */
    private void checkSeatWhenSeatAvailableForTheWholeTrip(final String customerId, final Journey journey,
    final BusTrip busTrip, final String seatNumber, final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap,
    final List<SeatBooking> availableSeats) {

        final SeatBooking seatBooking = this.createPendingSeatBooking(seatNumber, customerId, journey);

        final List<SeatBooking> seatBookingList = new ArrayList<>();

        seatBookingList.add(seatBooking);

        busTripBookingsMap.put(busTrip, seatBookingList);

        availableSeats.add(seatBooking);
    }

    /**
     * Create a Seat booking with Pending status
     *
     * @param seatNumber
     * @param customerId
     * @param journey
     * @return {@link SeatBooking}
     */
    private SeatBooking createPendingSeatBooking(final String seatNumber, final String customerId,
    final Journey journey) {

        return new SeatBooking(UUID.randomUUID(), seatNumber, customerId, BookingStatus.PENDING, journey);
    }
}
