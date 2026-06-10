package com.bus.seat.booking.controller;

import com.bus.seat.booking.service.SeatBookingService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class SeatBookingHandler extends ResponseHandler implements HttpHandler {

    private SeatBookingService seatBookingService;

    public SeatBookingHandler() {
        seatBookingService = new SeatBookingService();
    }

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {

        final String httpMethod = httpExchange.getRequestMethod();
        final String requestPath = httpExchange.getRequestURI().getPath();

        try {
            if ("GET".equalsIgnoreCase(httpMethod)
                    && "/api/bookings/check-availability".equalsIgnoreCase(requestPath)) {

            } else if ("POST".equalsIgnoreCase(httpMethod)
                    && "/api/bookings/confirm".equalsIgnoreCase(requestPath)) {


            } else {
                
            }
        } catch (Exception exception) {
            this.sendErrorResponse(httpExchange, 500,
                    "Internal Server Error: " + exception.getMessage());
        }
    }

    private void handleCheckAvailabilityRequest(final HttpExchange httpExchange) {

        final Headers requestHeaders = httpExchange.getRequestHeaders();

        final String origin = requestHeaders.getFirst("originCity");
        final String destination = requestHeaders.getFirst("destination");
        final String customerId = requestHeaders.getFirst("customerId");
        final String passengerCount = requestHeaders.getFirst("passengerCount");

        seatBookingService.checkSeatAvailability(origin, destination, )
    }
}
