package com.bus.seat.booking.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class DateUtils {

    /**
     * Convert date String to date
     *
     * @param dateString
     * @return {@link LocalDate}
     */
    public static LocalDate dateStringToDate(final String dateString) {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalDate date = null;

        try {
            date = LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException ignored) {
        }

        return date;
    }
}
