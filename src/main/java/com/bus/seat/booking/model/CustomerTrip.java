package com.bus.seat.booking.model;

import java.util.Arrays;
import java.util.List;

/**
 * Trip of the customer
 */
public enum CustomerTrip {

    AB(50.00, Arrays.asList(City.A), BusTrip.FIRST_TRIP), // Trip from A to B

    AC(100.00, Arrays.asList(City.A, City.B), BusTrip.FIRST_TRIP), // Trip from A to C

    AD(150.00, Arrays.asList(City.A, City.B, City.C), BusTrip.FIRST_TRIP), // Trip from A to D

    BA(50.00, Arrays.asList(City.B), BusTrip.RETURN_TRIP),// Trip from B to A

    BC(50.00, Arrays.asList(City.B), BusTrip.FIRST_TRIP), // Trip from B to C

    BD(100.00, Arrays.asList(City.B, City.C), BusTrip.FIRST_TRIP), // Trip from B to D

    CA(100.00, Arrays.asList(City.C, City.B), BusTrip.RETURN_TRIP), // Trip from C to A

    CB(50.00, Arrays.asList(City.C), BusTrip.RETURN_TRIP), // Trip from C to B

    CD(50.00, Arrays.asList(City.C), BusTrip.FIRST_TRIP), // Trip from C to D

    DC(50.00, Arrays.asList(City.D), BusTrip.RETURN_TRIP), // Trip from D to C

    DB(100.00, Arrays.asList(City.D, City.C), BusTrip.RETURN_TRIP), // Trip from D to B

    DA(150.00, Arrays.asList(City.D, City.C, City.B), BusTrip.RETURN_TRIP); // Tripe from D to A

    /**
     * Trip price
     */
    private final double tripPrice;

    /**
     * Cities that are unavailable for onboarding
     */
    private final List<City> citiesUnableToOnboard;

    /**
     * Bus trip of the customer trip
     */
    private final BusTrip busTrip;

    /**
     * @param tripPrice
     * @param citiesUnableToOnboard
     */
    private CustomerTrip(final double tripPrice, final List<City> citiesUnableToOnboard, final BusTrip busTrip) {
        this.tripPrice = tripPrice;
        this.citiesUnableToOnboard = citiesUnableToOnboard;
        this.busTrip = busTrip;
    }

    /**
     * @return tripPrice
     */
    public double getTripPrice() {
        return tripPrice;
    }

    /**
     * @return citiesUnableToOnboard
     */
    public List<City> getCitiesUnableToOnboard() {
        return citiesUnableToOnboard;
    }

    /**
     * @return busTrip
     */
    public BusTrip getBusTrip() {
        return busTrip;
    }
}
