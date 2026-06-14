package com.bus.seat.booking.controller;

import com.bus.seat.booking.configuration.DataInitializer;
import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.exceptions.BadRequestException;
import com.bus.seat.booking.exceptions.BookingExpiredException;
import com.bus.seat.booking.exceptions.NotFoundException;
import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatAvailabilityStatus;
import com.bus.seat.booking.model.Ticket;
import com.bus.seat.booking.service.BookingConfirmationService;
import com.bus.seat.booking.service.CheckAvailabilityService;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class SeatBookingHandlerTest {

    private SeatBookingHandler seatBookingHandler;

    private HttpExchange httpExchange;

    private final Gson gson;

    private final LocalDate testBookingDate;

    private final String testBookingDateString;

    SeatBookingHandlerTest() {
        testBookingDate = LocalDate.now();
        testBookingDateString = testBookingDate.toString();
        gson = new Gson();
    }

    @BeforeEach
    void beforeEach() {
        DataInitializer.BOOKED_SEATS.clear();
        seatBookingHandler = new SeatBookingHandler();
        httpExchange = Mockito.mock(HttpExchange.class);
    }

    @Test
    void testWhenCheckAvailabilityRequestReceivedThenHandlerShouldRedirectToCheckAvailabilityService()
    throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "1");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(200, outputStream.toString().length());

        final CheckAvailabilityResponse response =
                gson.fromJson(outputStream.toString(), CheckAvailabilityResponse.class);

        Assertions.assertEquals(SeatAvailabilityStatus.FULLY_AVAILABLE, response.getSeatAvailabilityStatus());
    }

    @Test
    void testWhenCheckAvailabilityErroredOriginThenShouldReturnBadRequest()
    throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // Origin city header is not available in headers
        final Headers headers = new Headers();
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "1");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        // Handler returns Bad request status
        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityErroredDestinationThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // Destination city header is not available in headers
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "1");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityErroredPassengerCountThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // Passenger count header is not available in headers
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityPassengerCountIsNotANumberThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // Passenger count header is available. But it is not a number
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "A");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityPassengerCountIsLessThanOneThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // Passenger count header is available. But it is less than 1
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "0");
        headers.set("date", testBookingDateString);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityNullDateStringThanOneThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // date is not available
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "1");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenCheckAvailabilityErroredDateStringThanOneThenShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/check-availability", null, null);

        // date header is available. But it is not in correct format
        final Headers headers = new Headers();
        headers.set("originCity", "A");
        headers.set("destinationCity", "B");
        headers.set("customerId", "customerId1");
        headers.set("passengerCount", "1");
        headers.set("date", "2026-6-14"); // Should be 2026-06-14

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getRequestHeaders()).thenReturn(headers);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingRequestReceivedThenHandlerShouldRedirectToConfirmBookingService()
    throws IOException, URISyntaxException {

        // Add booking to 1A seat from A to C
        final CheckAvailabilityResponse checkAvailabilityResponse =
                new CheckAvailabilityService().checkSeatAvailability(
                "A", "C", 1, "customer1", testBookingDateString);

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(checkAvailabilityResponse.getAvailableSeats());
        request.setBusTrip(checkAvailabilityResponse.getBusTrip());
        request.setBookingDate(checkAvailabilityResponse.getBookingDate());
        request.setTotalPrice(checkAvailabilityResponse.getTotalPrice());
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(200, outputStream.toString().length());

        final Ticket ticket =
                gson.fromJson(outputStream.toString(), Ticket.class);

        Assertions.assertEquals("1A", ticket.getBookedSeats().get(0));
    }

    @Test
    void testWhenConfirmBookingErroredReservedSeatsThenHandlerShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        // Reserved seats list is null
        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingErroredBusTripThenHandlerShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        // bus trip is null
        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingErroredTotalPriceThenHandlerShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        // Total price is zero in request
        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingErroredBookingDateThenHandlerShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        // Booking date is null in request
        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingInvalidBookingDateThenHandlerShouldReturnBadRequest()
            throws IOException, URISyntaxException {

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        // Booking date is available in the request. But it is errored
        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate("2026-6-14"); // Should be 2026-06-14
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingReturnNotFoundErrorThenHandlerShouldReturnNotFoundStatus()
            throws IOException, URISyntaxException, NoSuchFieldException, IllegalAccessException, NotFoundException,
            BadRequestException, BookingExpiredException {

        final BookingConfirmationService bookingConfirmationService = Mockito.mock(BookingConfirmationService.class);
        final Field field = SeatBookingHandler.class.getDeclaredField("bookingConfirmationService");
        field.setAccessible(true);
        field.set(seatBookingHandler, bookingConfirmationService);

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        // Mock BookingConfirmationService service to throw Not found Exception
        Mockito.when(bookingConfirmationService.confirmBooking(ArgumentMatchers.any(ConfirmBookingRequest.class)))
                .thenThrow(new NotFoundException("Not Found"));

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(404, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingReturnBookingExpiredErrorThenHandlerShouldReturnBadRequestStatus()
            throws IOException, URISyntaxException, NoSuchFieldException, IllegalAccessException, NotFoundException,
            BadRequestException, BookingExpiredException {

        final BookingConfirmationService bookingConfirmationService = Mockito.mock(BookingConfirmationService.class);
        final Field field = SeatBookingHandler.class.getDeclaredField("bookingConfirmationService");
        field.setAccessible(true);
        field.set(seatBookingHandler, bookingConfirmationService);

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        // Mock BookingConfirmationService service to throw Booking Expired Exception
        Mockito.when(bookingConfirmationService.confirmBooking(ArgumentMatchers.any(ConfirmBookingRequest.class)))
                .thenThrow(new BookingExpiredException("Booking expired"));

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingReturnBadRequestErrorThenHandlerShouldReturnBadRequestStatus()
            throws IOException, URISyntaxException, NoSuchFieldException, IllegalAccessException, NotFoundException,
            BadRequestException, BookingExpiredException {

        final BookingConfirmationService bookingConfirmationService = Mockito.mock(BookingConfirmationService.class);
        final Field field = SeatBookingHandler.class.getDeclaredField("bookingConfirmationService");
        field.setAccessible(true);
        field.set(seatBookingHandler, bookingConfirmationService);

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        // Mock BookingConfirmationService service to throw Bad Request Exception
        Mockito.when(bookingConfirmationService.confirmBooking(ArgumentMatchers.any(ConfirmBookingRequest.class)))
                .thenThrow(new BadRequestException("Bad Request"));

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(400, outputStream.toString().length());
    }

    @Test
    void testWhenConfirmBookingReturnUnknownErrorThenHandlerShouldReturnInternalServerErrorStatus()
            throws IOException, URISyntaxException, NoSuchFieldException, IllegalAccessException, NotFoundException,
            BadRequestException, BookingExpiredException {

        // Mock BookingConfirmationService
        final BookingConfirmationService bookingConfirmationService = Mockito.mock(BookingConfirmationService.class);
        final Field field = SeatBookingHandler.class.getDeclaredField("bookingConfirmationService");
        field.setAccessible(true);
        field.set(seatBookingHandler, bookingConfirmationService);

        final Map<String, UUID> reservedSeatsMap = new HashMap<>();
        reservedSeatsMap.put("1A", UUID.randomUUID());

        final ConfirmBookingRequest request = new ConfirmBookingRequest();
        request.setReservedSeats(reservedSeatsMap);
        request.setBusTrip(BusTrip.FIRST_TRIP);
        request.setBookingDate(testBookingDateString);
        request.setTotalPrice(100.0);
        request.setCustomerId("customerId1");

        final InputStream inputStream =
                new ByteArrayInputStream(gson.toJson(request).getBytes(StandardCharsets.UTF_8));

        final URI uri = new URI(null, null,
                "/api/bookings/confirm", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("POST");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getRequestBody()).thenReturn(inputStream);
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        // Mock BookingConfirmationService service to throw Runtime Exception
        Mockito.when(bookingConfirmationService.confirmBooking(ArgumentMatchers.any(ConfirmBookingRequest.class)))
                .thenThrow(new RuntimeException("Unknown Exception"));

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(500, outputStream.toString().length());
    }

    @Test
    void testWhenUnknownRequestReceivedThenHandlerShouldReturnMethodNotAllowedStatus()
            throws IOException, URISyntaxException {

        final URI uri = new URI(null, null,
                "/api/bookings/unknown", null, null);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Mockito.when(httpExchange.getRequestMethod()).thenReturn("GET");
        Mockito.when(httpExchange.getRequestURI()).thenReturn(uri);
        Mockito.when(httpExchange.getResponseHeaders()).thenReturn(new Headers());
        Mockito.when(httpExchange.getResponseBody()).thenReturn(outputStream);

        seatBookingHandler.handle(httpExchange);

        Mockito.verify(httpExchange).sendResponseHeaders(405, outputStream.toString().length());
    }
}