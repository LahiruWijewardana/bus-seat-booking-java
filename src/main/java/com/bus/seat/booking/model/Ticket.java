package com.bus.seat.booking.model;

import java.util.UUID;

public class Ticket {

    private UUID ticketId;

    private String customerId;

    private String journeyId;

    private Double totalPrice;

    public Ticket() {
        super();
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

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(final String journeyId) {
        this.journeyId = journeyId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(final Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
