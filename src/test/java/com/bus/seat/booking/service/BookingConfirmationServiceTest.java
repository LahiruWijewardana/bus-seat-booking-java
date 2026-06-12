package com.bus.seat.booking.service;

import com.bus.seat.booking.configuration.DataInitializer;
import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.exceptions.BadRequestException;
import com.bus.seat.booking.exceptions.BookingExpiredException;
import com.bus.seat.booking.exceptions.NotFoundException;
import com.bus.seat.booking.model.BookingStatus;
import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.City;
import com.bus.seat.booking.model.CustomerTrip;
import com.bus.seat.booking.model.Journey;
import com.bus.seat.booking.model.SeatBooking;
import com.bus.seat.booking.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

class BookingConfirmationServiceTest {

    private BookingConfirmationService bookingConfirmationService;

    private CheckAvailabilityService checkAvailabilityService;

    @BeforeEach
    void beforeEach() {
        DataInitializer.BOOKED_SEATS.clear();
        bookingConfirmationService = new BookingConfirmationService();
        checkAvailabilityService = new CheckAvailabilityService();
    }

    @Test
    void testWhenConfirmBookingThenConfirmBookingShouldReturnATicket()
    throws NotFoundException, BadRequestException, BookingExpiredException {

        // Add Booking for 1A seat. Origin A. Destination C
        final CheckAvailabilityResponse checkAvailabilityResponse = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 1, "customer1");

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(checkAvailabilityResponse.getAvailableSeats());
        request.setTotalPrice(checkAvailabilityResponse.getTotalPrice());
        request.setCustomerId("customerId1");

        // Confirm Booking
        final Ticket ticket = bookingConfirmationService.confirmBooking(request);

        final SeatBooking seatBooking = DataInitializer.BOOKED_SEATS.get("1A").get(BusTrip.FIRST_TRIP).get(0);

        // Booking status changed to BOOKED after confirmation
        Assertions.assertEquals(BookingStatus.BOOKED, seatBooking.getBookingStatus());
        Assertions.assertEquals("1A", ticket.getBookedSeats().get(0));
    }

    @Test
    void testWhenProvidedBookingCanNotBeFoundThenConfirmBookingShouldReturnANotFoundException() {

        // Add Booking for 1A seat. Origin A. Destination C
        final CheckAvailabilityResponse checkAvailabilityResponse = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 1, "customer1");

        // Create new Seat Booking with different seat booking id
        final SeatBooking seatBooking = new SeatBooking(UUID.randomUUID(), "1A", "customerId1",
                BookingStatus.PENDING, checkAvailabilityResponse.getAvailableSeats().get(0).getJourney());

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(Arrays.asList(seatBooking));
        request.setTotalPrice(checkAvailabilityResponse.getTotalPrice());
        request.setCustomerId("customerId1");

        // Confirm booking throws not found exception
        Assertions.assertThrows(NotFoundException.class, () -> bookingConfirmationService.confirmBooking(request));
    }

    @Test
    void testWhenProvidedBookingIsExpiredThenConfirmBookingShouldReturnBookingExpiredException() {

        // Add Booking for 1A seat. Origin A. Destination C
        final CheckAvailabilityResponse checkAvailabilityResponse = checkAvailabilityService.checkSeatAvailability(
                "A", "C", 1, "customer1");

        // Change seat booking create date time before 2 minutes
        final SeatBooking seatBooking = checkAvailabilityResponse.getAvailableSeats().get(0);
        seatBooking.setCreatedDateTime(Instant.now().minusSeconds(120));

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(checkAvailabilityResponse.getAvailableSeats());
        request.setTotalPrice(checkAvailabilityResponse.getTotalPrice());
        request.setCustomerId("customerId1");

        // Confirm booking throws Booking Expired exception
        Assertions.assertThrows(BookingExpiredException.class,
                () -> bookingConfirmationService.confirmBooking(request));
    }

    @Test
    void testWhenThereAreNoBookingsButProvidedAOneThenShouldReturnBadRequestException() {

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(new ArrayList<>());
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        // Confirm booking throws bad request exception
        Assertions.assertThrows(BadRequestException.class,
                () -> bookingConfirmationService.confirmBooking(request));
    }
}