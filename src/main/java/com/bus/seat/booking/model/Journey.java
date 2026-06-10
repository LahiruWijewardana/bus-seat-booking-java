package main.java.com.bus.seat.booking.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Journey {

    private UUID journeyId;

    private City origin;

    private City destination;

    private CustomerTrip customerTrip;

    private BusTrip busTrip;

    private List<City> bookedCities;

    private String estimatedArrival;

    public Journey() {
        super();
    }

    public UUID getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(final UUID journeyId) {
        this.journeyId = journeyId;
    }

    public City getOrigin() {
        return origin;
    }

    public void setOrigin(final City origin) {
        this.origin = origin;
    }

    public City getDestination() {
        return destination;
    }

    public void setDestination(final City destination) {
        this.destination = destination;
    }

    public CustomerTrip getCustomerTrip() {
        return customerTrip;
    }

    public void setCustomerTrip(final CustomerTrip customerTrip) {
        this.customerTrip = customerTrip;
    }

    public BusTrip getBusTrip() {
        return busTrip;
    }

    public void setBusTrip(final BusTrip busTrip) {
        this.busTrip = busTrip;
    }

    public List<City> getBookedCities() {
        return bookedCities;
    }

    public void setBookedCities(final List<City> bookedCities) {
        this.bookedCities = bookedCities;
    }

    public String getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(final String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }
}
