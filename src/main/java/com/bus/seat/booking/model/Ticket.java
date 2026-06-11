package com.bus.seat.booking.model;

import java.util.List;
import java.util.UUID;

public class Ticket {

    private UUID ticketId;

    private String customerId;

    private Journey journey;

    private List<String> bookedSeats;

    private Double totalPrice;

    public Ticket(final UUID ticketId, final String customerId, final Journey journey,
                  final List<String> bookedSeats, final Double totalPrice) {
        this.ticketId = ticketId;
        this.customerId = customerId;
        this.journey = journey;
        this.bookedSeats = bookedSeats;
        this.totalPrice = totalPrice;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(final UUID ticketId) {
        this.ticketId = ticketId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(final Journey journey) {
        this.journey = journey;
    }

    public List<String> getBookedSeats() {
        return bookedSeats;
    }

    public void setBookedSeats(final List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
