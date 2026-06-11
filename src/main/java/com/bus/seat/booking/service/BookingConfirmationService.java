package com.bus.seat.booking.service;

import com.bus.seat.booking.configuration.DataInitializer;
import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.exceptions.BadRequestException;
import com.bus.seat.booking.exceptions.NotFoundException;
import com.bus.seat.booking.model.BookingStatus;
import com.bus.seat.booking.model.SeatBooking;
import com.bus.seat.booking.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
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

            for (final SeatBooking seatBooking : reservedSeats) {

                final List<SeatBooking> seatBookingsListOfSeat =
                        DataInitializer.BOOKED_SEATS.get(seatBooking.getSeatNumber())
                                .get(seatBooking.getJourney().getBusTrip());

                final SeatBooking originalSeatBooking = seatBookingsListOfSeat.stream().filter(
                        booking -> booking.getSeatBookingId().equals(seatBooking.getSeatBookingId()))
                        .findFirst().orElse(null);

                if (originalSeatBooking != null) {
                    originalSeatBooking.setBookingStatus(BookingStatus.BOOKED);
                    bookedSeatNumbers.add(originalSeatBooking.getSeatNumber());
                    confirmedBookings.add(originalSeatBooking);
                } else {
                    logger.error("No booking found for the given seat booking id {}",
                            seatBooking.getSeatBookingId());
                    this.reverseBookingIfErrorOccurred(confirmedBookings);

                    throw new NotFoundException(
                            "NOT FOUND : No booking found for the given seat booking id "
                                    + seatBooking.getSeatBookingId());
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
     * Reverse Booking if any error occurred
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
}
