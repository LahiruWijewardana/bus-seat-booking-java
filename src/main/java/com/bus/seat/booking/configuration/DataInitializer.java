package com.bus.seat.booking.configuration;

import com.bus.seat.booking.model.BusTrip;
import com.bus.seat.booking.model.SeatBooking;
import com.bus.seat.booking.model.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataInitializer {

    /**
     * Map Seat number to booked list of Seat bookings
     * Ex:
     *      1A -> { FIRST_TRIP -> [ Booking1, Booking2 ], RETURN_TRIP -> [ Booking6 ] },
     *      1B -> { FIRST_TRIP -> [ Booking7 ], RETURN_TRIP -> [ Booking12 ] }
     *      ......
     *      10D -> { FIRST_TRIP -> [ Booking100 ], RETURN_TRIP -> [ Booking101 ] }
     */
    public static final ConcurrentMap<String, ConcurrentMap<BusTrip, List<SeatBooking>>> BOOKED_SEATS =
            new ConcurrentHashMap<>();

    /**
     * Map of ticket id to ticket
     */
    public static final Map<String, Ticket> TICKETS_MAP = new HashMap<>();

    /**
     * Reentrant Read Write lock to handle concurrent read writes
     */
    public static final ReentrantReadWriteLock READ_WRITE_LOCK = new ReentrantReadWriteLock();

    /**
     * Booking expiration time in seconds
     */
    public static final int BOOKING_EXPIRE_SECONDS = 60;
}
