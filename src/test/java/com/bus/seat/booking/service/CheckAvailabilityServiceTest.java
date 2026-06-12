package com.bus.seat.booking.service;

import com.bus.seat.booking.controller.response.CheckAvailabilityResponse;
import com.bus.seat.booking.model.BookingStatus;
import com.bus.seat.booking.model.SeatAvailabilityStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CheckAvailabilityServiceTest {

    private CheckAvailabilityService checkAvailabilityService;

    @BeforeEach
    void beforeEach() {
        checkAvailabilityService = new CheckAvailabilityService();
    }

    @Test
    void testWhenSeatBookedForOnePassengerAtoBThenCheckAvailabilityShouldReturnFullAvailable() {

        final CheckAvailabilityResponse response = checkAvailabilityService.checkSeatAvailability(
                "A", "B", 1, "customer1");

        Assertions.assertEquals(SeatAvailabilityStatus.FULLY_AVAILABLE, response.isSeatsAvailable());
    }

}