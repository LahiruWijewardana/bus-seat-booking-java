package com.bus.seat.booking.exceptions;

public class BookingExpiredException extends Exception {

    public BookingExpiredException() {
        super();
    }

    public BookingExpiredException(final String message) {
        super(message);
    }
}
