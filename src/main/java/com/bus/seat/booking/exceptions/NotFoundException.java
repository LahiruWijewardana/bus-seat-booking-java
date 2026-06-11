package com.bus.seat.booking.exceptions;

public class NotFoundException extends Exception{

    public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
