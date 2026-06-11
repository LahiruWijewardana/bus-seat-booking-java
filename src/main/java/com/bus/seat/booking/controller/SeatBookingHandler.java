package com.bus.seat.booking.controller;

import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.service.CheckAvailabilityService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SeatBookingHandler extends ResponseHandler implements HttpHandler {

    private final Logger logger = LogManager.getLogger(SeatBookingHandler.class);

    private final CheckAvailabilityService checkAvailabilityService;

    public SeatBookingHandler() {
        checkAvailabilityService = new CheckAvailabilityService();
    }

    @Override
    public void handle(final HttpExchange httpExchange) throws IOException {

        logger.info("Received a Request");

        final String httpMethod = httpExchange.getRequestMethod();
        final String requestPath = httpExchange.getRequestURI().getPath();

        logger.info("Request path : {}", requestPath);
        logger.info("Request method : {}", httpMethod);

        try {
            if ("GET".equalsIgnoreCase(httpMethod)
                    && "/api/bookings/check-availability".equalsIgnoreCase(requestPath)) {

                this.handleCheckAvailabilityRequest(httpExchange);

            } else if ("POST".equalsIgnoreCase(httpMethod)
                    && "/api/bookings/confirm".equalsIgnoreCase(requestPath)) {


            } else {
                
            }
        } catch (Exception exception) {
            logger.error(exception);
            this.sendErrorResponse(httpExchange, 500,
                    "Internal Server Error: " + exception.getMessage());
        }
    }

    private void handleCheckAvailabilityRequest(final HttpExchange httpExchange) throws IOException {

        logger.info("REQUEST RECEIVED - CHECK AVAILABILITY");

        final Headers requestHeaders = httpExchange.getRequestHeaders();

        final String origin = requestHeaders.getFirst("originCity");
        final String destination = requestHeaders.getFirst("destinationCity");
        final String customerId = requestHeaders.getFirst("customerId");
        final String passengerCount = requestHeaders.getFirst("passengerCount");

        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(origin, destination,
                Integer.parseInt(passengerCount), customerId);

        this.sendResponse(httpExchange, 200, response);
    }
}
