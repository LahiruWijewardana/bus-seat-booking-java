package com.bus.seat.booking.service;

import com.bus.seat.booking.configuration.DataInitializer;
import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.exceptions.BadRequestException;
import com.bus.seat.booking.exceptions.NotFoundException;
import com.bus.seat.booking.model.BookingStatus;
import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatBooking;
import com.bus.seat.booking.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BookingConfirmationService {

    private final Logger logger = LogManager.getLogger(BookingConfirmationService.class);

    /**
     * Confirm Booking of the reserved seats
     *
     * @param confirmBookingRequest
     * @return {@link Ticket}
     * @throws NotFoundException
     * @throws BadRequestException
     */
    public Ticket confirmBooking(final ConfirmBookingRequest confirmBookingRequest)
    throws NotFoundException, BadRequestException {

        final List<SeatBooking> reservedSeats = confirmBookingRequest.getReservedSeats();

        final List<String> bookedSeatNumbers = new ArrayList<>();
        final List<SeatBooking> confirmedBookings = new ArrayList<>();

        Ticket ticket;

        if (reservedSeats != null && !reservedSeats.isEmpty()) {

            for (final SeatBooking requestedSeatBooking : reservedSeats) {

                final SeatBooking originalSeatBooking =
                        this.retrieveOriginalSeatBooking(requestedSeatBooking, confirmedBookings);

                if (originalSeatBooking != null) {
                    originalSeatBooking.setBookingStatus(BookingStatus.BOOKED);
                    bookedSeatNumbers.add(originalSeatBooking.getSeatNumber());
                    confirmedBookings.add(originalSeatBooking);
                } else {
                    this.handleErrorIfNoRecords(requestedSeatBooking, confirmedBookings);
                }
            }

            final UUID ticketId = UUID.randomUUID();
            ticket = new Ticket(ticketId, confirmBookingRequest.getCustomerId(), reservedSeats.get(0).getJourney(),
                    bookedSeatNumbers, confirmBookingRequest.getTotalPrice());

            DataInitializer.TICKETS_MAP.put(ticketId.toString(), ticket);

            logger.info("BOOKING CONFIRMED - TICKET ID : {}", ticketId.toString());

        } else {
            logger.error("reservedSeats List is NULL or EMPTY");
            throw new BadRequestException("BAD REQUEST : reservedSeats List is NULL or EMPTY");

        }

        return ticket;
    }

    /**
     * Retrieve Original Seat booking from saved data set
     *
     * @param requestedSeatBooking
     * @param confirmedBookings
     * @return Original {@link SeatBooking}
     * @throws NotFoundException
     */
    private SeatBooking retrieveOriginalSeatBooking(final SeatBooking requestedSeatBooking,
    final List<SeatBooking> confirmedBookings) throws NotFoundException {

        final Map<BusTrip, List<SeatBooking>> busTripToBookingsMap =
                DataInitializer.BOOKED_SEATS.get(requestedSeatBooking.getSeatNumber());

        if (busTripToBookingsMap == null || busTripToBookingsMap.isEmpty()) {
            this.handleErrorIfNoRecords(requestedSeatBooking, confirmedBookings);
        }

        final List<SeatBooking> seatBookingsListOfSeat =
                busTripToBookingsMap.get(requestedSeatBooking.getJourney().getBusTrip());

        if (seatBookingsListOfSeat == null || seatBookingsListOfSeat.isEmpty()) {
            this.handleErrorIfNoRecords(requestedSeatBooking, confirmedBookings);
        }

        return seatBookingsListOfSeat.stream().filter(
                        booking -> booking.getSeatBookingId().equals(requestedSeatBooking.getSeatBookingId()))
                .findFirst().orElse(null);
    }

    /**
     * Reverse Confirmed Bookings if any error occurred
     *
     * @param confirmedBookings
     */
    private void reverseBookingIfErrorOccurred(final List<SeatBooking> confirmedBookings) {

        if (confirmedBookings != null && !confirmedBookings.isEmpty()) {

            for (final SeatBooking seatBooking : confirmedBookings) {

                seatBooking.setBookingStatus(BookingStatus.PENDING);
            }
        }
    }

    /**
     * Handle error if no record found for the given seat booking
     *
     * @param requestedSeatBooking
     * @param confirmedBookings
     * @throws NotFoundException
     */
    private void handleErrorIfNoRecords(final SeatBooking requestedSeatBooking,
    final List<SeatBooking> confirmedBookings) throws NotFoundException {
        logger.error("No booking found for the given seat booking id {}",
                requestedSeatBooking.getSeatBookingId());
        this.reverseBookingIfErrorOccurred(confirmedBookings);

        throw new NotFoundException(
                "NOT FOUND : No booking found for the given seat booking id "
                        + requestedSeatBooking.getSeatBookingId());
    }
}
