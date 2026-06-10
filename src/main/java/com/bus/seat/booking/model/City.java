package com.bus.seat.booking.model;

public enum City {

    A("8.00 am", "5.00 pm"),

    B("9.00 am", "4.00 pm"),

    C("10.00 am", "3.00 pm"),

    D("11.00 am", "2.00 pm");

    /**
     */
    private final String firstTripEstimatedArrivalTime;

    /**
     */
    private final String returnTripEstimatedArrivalTime;

    /**
     * @param firstTripEstimatedArrivalTime
     * @param returnTripEstimatedArrivalTime
     */
    private City(final String firstTripEstimatedArrivalTime, final String returnTripEstimatedArrivalTime) {
        this.firstTripEstimatedArrivalTime = firstTripEstimatedArrivalTime;
        this.returnTripEstimatedArrivalTime = returnTripEstimatedArrivalTime;
    }

    /**
     * @return firstTripEstimatedArrivalTime
     */
    public String getFirstTripEstimatedArrivalTime() {
        return firstTripEstimatedArrivalTime;
    }

    /**
     * @return returnTripEstimatedArrivalTime
     */
    public String getReturnTripEstimatedArrivalTime() {
        return returnTripEstimatedArrivalTime;
    }
}
