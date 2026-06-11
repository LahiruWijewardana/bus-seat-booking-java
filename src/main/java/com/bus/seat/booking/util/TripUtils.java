package com.bus.seat.booking.util;

import com.bus.seat.booking.model.City;
import com.bus.seat.booking.model.CustomerTrip;

public class TripUtils {

    /**
     * Determine City from city String
     *
     * @param cityString
     * @return {@link City}
     */
    public static City determineCityFromCityString(final String cityString) {
        return City.valueOf(cityString.trim().toUpperCase());
    }

    /**
     * Determine Customer trip
     *
     * @param originCity
     * @param destinationCity
     * @return {@link CustomerTrip}
     */
    public static CustomerTrip determineCustomerTrip(final City originCity, final City destinationCity) {

        final String tripString = originCity.name() + destinationCity.name();
        return CustomerTrip.valueOf(tripString);
    }
}
