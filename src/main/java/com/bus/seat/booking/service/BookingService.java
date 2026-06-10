package main.java.com.bus.seat.booking.service;

import main.java.com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import main.java.com.bus.seat.booking.model.*;

import java.util.*;

public class BookingService {

    /**
     * Map Seat number to booked list of Seat bookings
     * Ex:
     *      1A -> { FIRST_TRIP -> [ Booking1, Booking2 ], RETURN_TRIP -> [ Booking6 ] },
     *      1B -> { FIRST_TRIP -> [ Booking7 ], RETURN_TRIP -> [ Booking12 ] }
     *      ......
     *      10D -> { FIRST_TRIP -> [ Booking100 ], RETURN_TRIP -> [ Booking101 ] }
     */
    public static final Map<String, Map<BusTrip, List<SeatBooking>>> BOOKED_SEATS = new HashMap<>();

    /**
     * Map Seat number to Seat booked status for the trip
     * Ex:
     *      1A -> { AB -> TRUE, BC -> FALSE, ..., BA -> TRUE }
     *      1B -> { AB -> FALSE, BC -> TRUE, ..., BA -> TRUE }
     *      ......
     *      10D -> { AB -> TRUE, BC -> FALSE,..., BA -> FALSE }
     */
    public static final Map<String, Map<BusTrip, List<City>>> UNAVAILABLE_CITIES_FOR_SEAT = new HashMap<>();

    private static final int TOTAL_SEAT_COUNT = 40;

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

        final CustomerTrip customerTrip = this.determineCustomerTrip(origin, destination);
        final City originCity = City.valueOf(origin);
        final City destinationCity = City.valueOf(destination);
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

                final String seatNumber = seatRow + seatColumn;

                if (BOOKED_SEATS.containsKey(seatNumber)) {

                    final Map<BusTrip, List<SeatBooking>> busTripBookingsMap = BOOKED_SEATS.get(seatNumber);

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

        } else if (availableSeatCount < passengerCount) {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.PARTIALLY_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(availableSeatCount);
            checkAvailabilityResponse.setTotalPrice(customerTrip.getTripPrice() * availableSeatCount);

        } else {

            checkAvailabilityResponse.setSeatAvailabilityStatus(SeatAvailabilityStatus.NOT_AVAILABLE);
            checkAvailabilityResponse.setPassengerCount(0);
            checkAvailabilityResponse.setTotalPrice(0.00);
        }

        return checkAvailabilityResponse;
    }

    /**
     * Determine the Customer Trip Enum from origin and destination
     *
     * @param origin
     * @param destination
     * @return {@link CustomerTrip}
     */
    private CustomerTrip determineCustomerTrip(final String origin, final String destination) {

        final String tripString = origin.trim() + destination.trim();
        return CustomerTrip.valueOf(tripString);
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

        final Map<BusTrip, List<SeatBooking>> busTripToBookingsMap = new HashMap<>();
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
    final Map<BusTrip, List<SeatBooking>> busTripBookingsMap, final List<SeatBooking> availableSeats) {

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
    final BusTrip busTrip, final String seatNumber, final Map<BusTrip, List<SeatBooking>> busTripBookingsMap,
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
