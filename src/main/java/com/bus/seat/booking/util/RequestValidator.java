package com.bus.seat.booking.util;

import com.bus.seat.booking.controller.request.ConfirmBookingRequest;
import com.bus.seat.booking.exceptions.BadRequestException;

import java.time.LocalDate;

public class RequestValidator {

    /**
     * Validate Check Availability request parameters
     *
     * @param originCity
     * @param destinationCity
     * @param passengerCount
     * @param customerId
     * @param dateString
     * @throws BadRequestException
     */
    public static void validateCheckAvailabilityRequest(final String originCity, final String destinationCity,
    final String passengerCount, final String customerId, final String dateString) throws BadRequestException {

        if (checkIfValidString(originCity)) {
            throw new BadRequestException("originCity can not be NULL or EMPTY");
        }

        if (checkIfValidString(destinationCity)) {
            throw new BadRequestException("destinationCity can not be NULL or EMPTY");
        }

        if (checkIfValidString(customerId)) {
            throw new BadRequestException("customerId can not be NULL or EMPTY");
        }

        if (checkIfValidString(passengerCount)) {
            throw new BadRequestException("passengerCount can not be NULL or EMPTY");
        }

        final int passengerCountNumber;

        try {
            passengerCountNumber = Integer.parseInt(passengerCount);
        } catch (NumberFormatException e) {
            throw new BadRequestException("passengerCount should be an Integer");
        }

        if (passengerCountNumber < 1) {
            throw new BadRequestException("passengerCount should be greater than zero");
        }

        if (checkIfValidString(dateString)) {
            throw new BadRequestException("dateString can not be NULL or EMPTY");
        }

        final LocalDate date = DateUtils.dateStringToDate(dateString);

        if (date == null) {
            throw new BadRequestException("Invalid dateString. Date should be in yyyy-MM-dd format");
        }

        if (!(LocalDate.now().equals(date) || LocalDate.now().isBefore(date))) {
            throw new BadRequestException("bookingDate should equal to today or future date");
        }
    }

    /**
     * Validate Confirm Booking request
     *
     * @param request
     * @throws BadRequestException
     */
    public static void validateConfirmBookingRequest(final ConfirmBookingRequest request) throws BadRequestException {

        if (!(request.getReservedSeats() != null && !request.getReservedSeats().isEmpty())) {
            throw new BadRequestException("reservedSeats can not be NULL or Empty");
        }

        if (request.getBusTrip() == null) {
            throw new BadRequestException("busTrip can not be NULL");
        }

        if (request.getTotalPrice() <= 0) {
            throw new BadRequestException("totalPrice should be greater than zero");
        }

        if (checkIfValidString(request.getCustomerId())) {
            throw new BadRequestException("customerId can not be NULL or EMPTY");
        }

        if (checkIfValidString(request.getBookingDate())) {
            throw new BadRequestException("bookingDate can not be NULL or EMPTY");
        }

        final LocalDate date = DateUtils.dateStringToDate(request.getBookingDate());

        if (date == null) {
            throw new BadRequestException("Invalid bookingDate. Date should be in yyyy-MM-dd format");
        }

        if (!(LocalDate.now().equals(date) || LocalDate.now().isBefore(date))) {
            throw new BadRequestException("bookingDate should equal to today or future date");
        }
    }

    /**
     * Check if provided string is NULL or Empty
     *
     * @param requestParameterString
     * @return TRUE if valid String
     */
    private static boolean checkIfValidString(final String requestParameterString) {
        boolean isValidString = false;

        if (requestParameterString != null && !requestParameterString.isEmpty()) {
            isValidString = true;
        }

        return !isValidString;
    }
}
