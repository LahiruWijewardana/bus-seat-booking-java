package com.bus.seat.booking.exceptions;

public class BadRequestException extends Exception {

    public BadRequestException() {
        super();
    }

    public BadRequestException(final String message) {
        super(message);
    }
}
