package com.bus.seat.booking.controller;

import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.exceptions.BadRequestException;
import com.bus.seat.booking.exceptions.NotFoundException;
import com.bus.seat.booking.model.Ticket;
import com.bus.seat.booking.service.BookingConfirmationService;
import com.bus.seat.booking.service.CheckAvailabilityService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SeatBookingHandler extends ResponseHandler implements HttpHandler {

    private final Logger logger = LogManager.getLogger(SeatBookingHandler.class);

    private final CheckAvailabilityService checkAvailabilityService;

    private final BookingConfirmationService bookingConfirmationService;

    private final Gson gson = new Gson();

    public SeatBookingHandler() {
        checkAvailabilityService = new CheckAvailabilityService();
        bookingConfirmationService = new BookingConfirmationService();
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

                this.handleConfirmBookingRequest(httpExchange);

            } else {

                this.sendErrorResponse(httpExchange, 405, "Method not allowed");
            }

        } catch (NotFoundException notFoundException) {
            logger.error(notFoundException);
            this.sendErrorResponse(httpExchange, 404, notFoundException.getMessage());

        } catch (BadRequestException badRequestException) {
            logger.error(badRequestException);
            this.sendErrorResponse(httpExchange, 400, badRequestException.getMessage());

        } catch (Exception exception) {
            logger.error(exception);
            this.sendErrorResponse(httpExchange, 500,
                    "Internal Server Error: " + exception.getMessage());
        }
    }

    /**
     * Handle Check availability request
     *
     * @param httpExchange
     * @throws IOException
     */
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

    /**
     * Handle Confirm booking request
     *
     * @param httpExchange
     * @throws IOException
     * @throws NotFoundException
     * @throws BadRequestException
     */
    private void handleConfirmBookingRequest(final HttpExchange httpExchange) throws IOException, NotFoundException, BadRequestException {

        logger.info("REQUEST RECEIVED - CONFIRM BOOKING");

        final String requestBody = this.readRequestBody(httpExchange);

        if (requestBody == null || requestBody.isEmpty()) {
            this.sendErrorResponse(httpExchange, 400, "Request Body is empty");
        }

        final ConfirmBookingRequest confirmBookingRequest = gson.fromJson(requestBody, ConfirmBookingRequest.class);

        final Ticket ticket = bookingConfirmationService.confirmBooking(confirmBookingRequest);

        this.sendResponse(httpExchange, 200, ticket);
    }
}
