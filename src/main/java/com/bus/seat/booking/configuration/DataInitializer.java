package com.bus.seat.booking.configuration;

import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatBooking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInitializer {

    /**
     * Map Seat number to booked list of Seat bookings
     * Ex:
     *      1A -> { FIRST_TRIP -> [ Booking1, Booking2 ], RETURN_TRIP -> [ Booking6 ] },
     *      1B -> { FIRST_TRIP -> [ Booking7 ], RETURN_TRIP -> [ Booking12 ] }
     *      ......
     *      10D -> { FIRST_TRIP -> [ Booking100 ], RETURN_TRIP -> [ Booking101 ] }
     */
    public static final Map<String, Map<BusTrip, List<SeatBooking>>> BOOKED_SEATS = new HashMap<>();
}
