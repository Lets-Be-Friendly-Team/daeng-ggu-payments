package com.ureca.daengggupayments.config.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class DateTimeUtil {

    /**
     * yyyy-MM-dd'T'HH:mm:ss±hh:mm ISO 8601 형식 변환
     *
     * @param dateTimeString
     * @return dateTime
     */
    public static LocalDateTime parseToLocalDateTime(String dateTimeString) {
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeString);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateTimeString, e);
        }
    }
}
