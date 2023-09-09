package hu.bmiklos.bc.service.mapper;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.service.dto.EventDto;

public class EventMapper {

    private EventMapper() {}

    public static EventDto mapToDto(Event lastEvent) {
        return new EventDto(lastEvent.getBookId(), lastEvent.getTime(), lastEvent.getHostId());
    }
}
