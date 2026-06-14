package com.bus.seat.booking.controller.request;

import com.bus.seat.booking.model.BusTrip;

import java.util.Map;
import java.util.UUID;

public class ConfirmBookingRequest {

    private Map<String, UUID> reservedSeats;

    private String bookingDate;

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

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(final String bookingDate) {
        this.bookingDate = bookingDate;
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
