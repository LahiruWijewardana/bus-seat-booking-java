package com.bus.seat.booking.controller.request;

import com.bus.seat.booking.model.SeatBooking;

import java.util.List;

public class ConfirmBookingRequest {

    private List<SeatBooking> reservedSeats;

    private double totalPrice;

    private String customerId;

    public ConfirmBookingRequest() {
        super();
    }

    public List<SeatBooking> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(final List<SeatBooking> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }
}
