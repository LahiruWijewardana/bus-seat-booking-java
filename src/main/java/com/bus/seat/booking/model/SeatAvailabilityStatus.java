package main.java.com.bus.seat.booking.model;

public enum SeatAvailabilityStatus {

    FULLY_AVAILABLE, // Requested seat count is available

    PARTIALLY_AVAILABLE, // Available seat count is less than requested seat count

    NOT_AVAILABLE // Seats are not available
}
