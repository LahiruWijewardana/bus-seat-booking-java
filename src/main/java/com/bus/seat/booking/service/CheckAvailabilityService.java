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
import com.bus.seat.booking.util.DateUtils;
import com.bus.seat.booking.util.TripUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bus.seat.booking.configuration.DataInitializer.BOOKED_SEATS;

public class CheckAvailabilityService {

    private final Logger logger = LogManager.getLogger(CheckAvailabilityService.class);

    private static final List<String> SEAT_COLUMNS = Arrays.asList("A", "B", "C", "D");

    private final Gson gson;

    public CheckAvailabilityService() {
        super();
        gson = new Gson();
    }

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
    final int passengerCount, final String customerId, final String dateString) {

        logger.info("ORIGIN: {}, DESTINATION: {}, PASSENGER_COUNT: {}, DATE: {}, CUSTOMER_ID: {}",
                origin, destination, passengerCount, dateString, customerId);

        final City originCity = TripUtils.determineCityFromCityString(origin);
        final City destinationCity = TripUtils.determineCityFromCityString(destination);
        final LocalDate bookingDate = DateUtils.dateStringToDate(dateString);

        final CustomerTrip customerTrip = TripUtils.determineCustomerTrip(originCity, destinationCity);
        final BusTrip busTrip = customerTrip.getBusTrip();

        final Journey journey = new Journey();
        journey.setJourneyId(UUID.randomUUID());
        journey.setOrigin(originCity);
        journey.setDestination(destinationCity);
        journey.setCustomerTrip(customerTrip);
        journey.setBusTrip(busTrip);
        journey.setBookedCities(customerTrip.getCitiesUnableToOnboard());
        journey.setEstimatedDeparture(busTrip == BusTrip.FIRST_TRIP
                ? originCity.getFirstTripEstimatedArrivalTime() : originCity.getReturnTripEstimatedArrivalTime());
        journey.setEstimatedArrival(busTrip == BusTrip.FIRST_TRIP
                ? destinationCity.getFirstTripEstimatedArrivalTime()
                : destinationCity.getReturnTripEstimatedArrivalTime());

        final Map<String, UUID> availableSeats = new HashMap<>();

        for (int seatRow = 1; seatRow <= 10; seatRow++) {

            for (final String seatColumn : SEAT_COLUMNS) {

                try {
                    DataInitializer.READ_WRITE_LOCK.writeLock().lock();

                    final String seatNumber = seatRow + seatColumn;

                    if (BOOKED_SEATS.containsKey(bookingDate)) {

                        final ConcurrentMap<String, ConcurrentMap<BusTrip, List<SeatBooking>>> dateToSeatsMap =
                                BOOKED_SEATS.get(bookingDate);

                        if (dateToSeatsMap.containsKey(seatNumber)) {

                            final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap =
                                    dateToSeatsMap.get(seatNumber);

                            if (busTripBookingsMap.containsKey(busTrip)) {

                                this.checkSeatWhenBookingsAreAvailable(customerId, journey, busTrip, customerTrip,
                                        seatNumber, busTripBookingsMap, availableSeats, bookingDate);

                            } else {

                                this.checkSeatWhenSeatAvailableForTheWholeTrip(customerId, journey, busTrip, seatNumber,
                                        busTripBookingsMap, availableSeats, bookingDate);
                            }

                        } else {

                            this.checkSeatWhenWholeSeatIsAvailable(customerId, journey, busTrip, seatNumber,
                                    availableSeats, dateToSeatsMap, bookingDate);

                        }

                    } else {

                        this.checkSeatWhenWholeSeatAvailableForTheDate(
                                customerId, journey, busTrip, seatNumber, availableSeats, bookingDate);

                    }

                } finally {
                    DataInitializer.READ_WRITE_LOCK.writeLock().unlock();
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
        checkAvailabilityResponse.setBookingDate(dateString);
        checkAvailabilityResponse.setBusTrip(busTrip);

        if (availableSeatCount == passengerCount) {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.FULLY_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(passengerCount);
            checkAvailabilityResponse.setTotalPrice(customerTrip.getTripPrice() * passengerCount);
            checkAvailabilityResponse.setAvailableSeats(availableSeats);

        } else if (availableSeatCount != 0 && availableSeatCount < passengerCount) {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.PARTIALLY_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(availableSeatCount);
            checkAvailabilityResponse.setTotalPrice(customerTrip.getTripPrice() * availableSeatCount);
            checkAvailabilityResponse.setAvailableSeats(availableSeats);

        } else {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.NOT_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(0);
            checkAvailabilityResponse.setTotalPrice(0.00);
        }

        logger.info("CHECK AVAILABILITY RESPONSE: {}", gson.toJson(checkAvailabilityResponse));

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
     * @param dateToSeatsMap
     * @param bookingDate
     */
    private void checkSeatWhenWholeSeatIsAvailable(final String customerId, final Journey journey,
    final BusTrip busTrip, final String seatNumber, final Map<String, UUID> availableSeats,
    final ConcurrentMap<String, ConcurrentMap<BusTrip, List<SeatBooking>>> dateToSeatsMap,
    final LocalDate bookingDate) {

        final SeatBooking seatBooking = this.createPendingSeatBooking(seatNumber, customerId, journey, bookingDate);

        final ConcurrentMap<BusTrip, List<SeatBooking>> busTripToBookingsMap = new ConcurrentHashMap<>();
        busTripToBookingsMap.put(busTrip, new ArrayList<>(Arrays.asList(seatBooking)));

        dateToSeatsMap.put(seatNumber, busTripToBookingsMap);

        availableSeats.put(seatNumber, seatBooking.getSeatBookingId());
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
     * @param bookingDate
     */
    private void checkSeatWhenBookingsAreAvailable(final String customerId, final Journey journey,
    final BusTrip busTrip, final CustomerTrip customerTrip, final String seatNumber,
    final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap, final Map<String, UUID> availableSeats,
    final LocalDate bookingDate) {

        final List<SeatBooking> seatBookingList = busTripBookingsMap.get(busTrip);

        final Set<City> unableToOnboardCitiesSet =
                new HashSet<>(customerTrip.getCitiesUnableToOnboard());

        final boolean bookingAvailable = seatBookingList.stream()
                .anyMatch(seatBooking -> (seatBooking.getBookingStatus() == BookingStatus.PENDING
                        && seatBooking.getCreatedDateTime().plusSeconds(DataInitializer.BOOKING_EXPIRE_SECONDS)
                        .isBefore(Instant.now()))
                                || seatBooking.getJourney().getBookedCities().stream()
                                .noneMatch(unableToOnboardCitiesSet::contains));

        if (bookingAvailable) {

            final SeatBooking seatBooking = this.createPendingSeatBooking(
                    seatNumber, customerId, journey, bookingDate);

            seatBookingList.add(seatBooking);

            availableSeats.put(seatNumber, seatBooking.getSeatBookingId());
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
     * @param bookingDate
     */
    private void checkSeatWhenSeatAvailableForTheWholeTrip(final String customerId, final Journey journey,
    final BusTrip busTrip, final String seatNumber, final ConcurrentMap<BusTrip, List<SeatBooking>> busTripBookingsMap,
    final Map<String, UUID> availableSeats, final LocalDate bookingDate) {

        final SeatBooking seatBooking = this.createPendingSeatBooking(seatNumber, customerId, journey, bookingDate);

        final List<SeatBooking> seatBookingList = new ArrayList<>();

        seatBookingList.add(seatBooking);

        busTripBookingsMap.put(busTrip, seatBookingList);

        availableSeats.put(seatNumber, seatBooking.getSeatBookingId());
    }

    /**
     * Check seat when all seats are available for the date
     *
     * @param customerId
     * @param journey
     * @param busTrip
     * @param seatNumber
     * @param availableSeats
     * @param bookingDate
     */
    private void checkSeatWhenWholeSeatAvailableForTheDate(final String customerId, final Journey journey,
    final BusTrip busTrip, final String seatNumber, final Map<String, UUID> availableSeats,
    final LocalDate bookingDate) {

        final ConcurrentMap<String, ConcurrentMap<BusTrip, List<SeatBooking>>> dateToSeatsMap =
                new ConcurrentHashMap<>();

        this.checkSeatWhenWholeSeatIsAvailable(
                customerId, journey, busTrip, seatNumber, availableSeats, dateToSeatsMap, bookingDate);

        BOOKED_SEATS.put(bookingDate, dateToSeatsMap);
    }

    /**
     * Create a Seat booking with Pending status
     *
     * @param seatNumber
     * @param customerId
     * @param journey
     * @param bookingDate
     * @return {@link SeatBooking}
     */
    private SeatBooking createPendingSeatBooking(final String seatNumber, final String customerId,
    final Journey journey, final LocalDate bookingDate) {

        return new SeatBooking(UUID.randomUUID(), seatNumber, customerId, BookingStatus.PENDING,
                journey, bookingDate);
    }
}
