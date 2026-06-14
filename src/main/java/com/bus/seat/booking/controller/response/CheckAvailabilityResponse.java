package com.bus.seat.booking.controller.response;

import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatAvailabilityStatus;

import java.util.Map;
import java.util.UUID;

public class CheckAvailabilityResponse {

    private SeatAvailabilityStatus seatAvailabilityStatus;

    private String origin;

    private String destination;

    private String bookingDate;

    private int passengerCount;

    private double totalPrice;

    private Map<String, UUID> availableSeats;

    private BusTrip busTrip;

    public CheckAvailabilityResponse() {
        super();
    }

    public SeatAvailabilityStatus getSeatAvailabilityStatus() {
        return seatAvailabilityStatus;
    }

    public void setSeatAvailabilityStatus(final SeatAvailabilityStatus seatAvailabilityStatus) {
        this.seatAvailabilityStatus = seatAvailabilityStatus;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(final String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(final int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<String, UUID> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(final Map<String, UUID> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BusTrip getBusTrip() {
        return busTrip;
    }

    public void setBusTrip(final BusTrip busTrip) {
        this.busTrip = busTrip;
    }
}
