package hu.bmiklos.bc.service.mapper;

import java.time.Instant;
import java.util.List;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.Participant;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class EventMapper {

    private EventMapper() {}

    public static CreateEventDto mapToDto(Event lastEvent) {
        return new CreateEventDto(lastEvent.getBookId(), lastEvent.getTime(), lastEvent.getHostId());
    }

    public static GetEventDto mapToGetDto(Event event) {
        Instant time = event.getTime();
        BookDto book = BookMapper.mapToDto(event.getBook());
        UserDto host = null;
        if (event.getHostByHostId() != null) {
            host = UserMapper.mapToDto(event.getHostByHostId());
        }
        if (event.getHostByHostExternalId() != null) {
            host = UserMapper.mapToDto(event.getHostByHostExternalId());
        }        
        List<UserDto> participants = event.getParticipants()
            .stream()
            .map(Participant::getUser)
            .map(UserMapper::mapToDto)
            .toList();
        return new GetEventDto(time, book, host, event.getHostExternalId(), participants);
    }
}
