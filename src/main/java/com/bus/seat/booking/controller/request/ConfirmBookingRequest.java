package com.bus.seat.booking.controller.request;

import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatBooking;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfirmBookingRequest {

    private Map<String, UUID> reservedSeats;

    private BusTrip busTrip;

    private double totalPrice;

    private String customerId;

    public ConfirmBookingRequest() {
        super();
    }

    public Map<String, UUID> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(final Map<String, UUID> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public BusTrip getBusTrip() {
        return busTrip;
    }

    public void setBusTrip(final BusTrip busTrip) {
        this.busTrip = busTrip;
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
