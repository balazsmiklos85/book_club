package hu.bmiklos.bc.controller.mapper;

import java.time.Instant;
import java.util.UUID;

import hu.bmiklos.bc.controller.dto.CreateEventRequest;
import hu.bmiklos.bc.service.dto.EventDto;

public class EventMapper {

    private EventMapper() {}

    public static EventDto mapToDto(CreateEventRequest event) {
        UUID mappedBookId = UUID.fromString(event.getBookId());
        Instant mappedTime = DateTimeMapper.toInstant(event.getDate(), event.getTime());
        UUID mappedHostId = UUID.fromString(event.getHost());

        return new EventDto(mappedBookId, mappedTime, mappedHostId);
    }
}
