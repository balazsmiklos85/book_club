package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

class DateTimeMapperTest {
    @Test
    void mapsDateAndTimeToInstant() {
        var inputDate = "2023-09-22";
        var inputTime = "18:00";

        Instant asTime = DateTimeMapper.toInstant(inputDate, inputTime);
        
        LocalDateTime inLocalTimeZone = asTime.atZone(ZoneId.systemDefault())
            .toLocalDateTime();

        assertEquals(2023, inLocalTimeZone.getYear(), "Parsing the year failed.");
        assertEquals(9, inLocalTimeZone.getMonthValue(), "Parsing the month failed.");
        assertEquals(22, inLocalTimeZone.getDayOfMonth(), "Parsing the day failed.");
        assertEquals(18, inLocalTimeZone.getHour(), "Parsing the hour failed.");
        assertEquals(0, inLocalTimeZone.getMinute(), "Parsing the minute failed.");
    
        if (ZoneId.systemDefault().getRules().getOffset(asTime).getTotalSeconds() != 0) {
            LocalDateTime inUtc = asTime.atZone(ZoneOffset.UTC)
                .toLocalDateTime();

            assertNotEquals(18, inUtc.getHour(), "Parsing the date and time did not result in a UTC time.");
            assertEquals(16, inUtc.getHour(), "Parsing the date and time did not result in a UTC time.");
        }
    }
}
