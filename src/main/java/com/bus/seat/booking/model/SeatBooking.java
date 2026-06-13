package com.bus.seat.booking.model;

import java.time.Instant;
import java.util.UUID;

public class SeatBooking {

    private UUID seatBookingId;

    private String seatNumber;

    private String customerId;

    private String ticketId;

    private Journey journey;

    private BookingStatus bookingStatus;

    private Instant bookedDateTime;

    private Instant createdDateTime;

    public SeatBooking() {
        super();
    }

    public SeatBooking(final UUID seatBookingId, final String seatNumber, final String customerId,
                       final BookingStatus bookingStatus, final Journey journey) {
        super();

        this.seatBookingId = seatBookingId;
        this.customerId = customerId;
        this.seatNumber = seatNumber;
        this.bookingStatus = bookingStatus;
        this.journey = journey;
        this.createdDateTime = Instant.now();
    }

    public UUID getSeatBookingId() {
        return seatBookingId;
    }

    public void setSeatBookingId(final UUID seatBookingId) {
        this.seatBookingId = seatBookingId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(final String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(final String ticketId) {
        this.ticketId = ticketId;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(final Journey journey) {
        this.journey = journey;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(final BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Instant getBookedDateTime() {
        return bookedDateTime;
    }

    public void setBookedDateTime(final Instant bookedDateTime) {
        this.bookedDateTime = bookedDateTime;
    }

    public Instant getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(final Instant createdDateTime) {
        this.createdDateTime = createdDateTime;
    }
}
