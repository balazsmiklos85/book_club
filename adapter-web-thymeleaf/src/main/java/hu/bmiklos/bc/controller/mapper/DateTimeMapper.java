package hu.bmiklos.bc.controller.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateTimeMapper {
    private DateTimeMapper() {
    }

    public static Instant toInstant(String date, String time) {
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_DATE)
            .appendLiteral(' ')
            .append(DateTimeFormatter.ISO_TIME)
            .toFormatter();

        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, formatter);
        return dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String toLocalDateString(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate().toString();
    }

    public static String toLocalTimeString(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime().toString();
    }
}
