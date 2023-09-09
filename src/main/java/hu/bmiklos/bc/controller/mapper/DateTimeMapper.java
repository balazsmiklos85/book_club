package hu.bmiklos.bc.controller.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTimeMapper {
    private DateTimeMapper() {
    }

    public static Instant toInstant(String date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, formatter);
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    public static String toLocalDateString(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate().toString();
    }

    public static String toLocalTimeString(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalTime().toString();
    }
}
