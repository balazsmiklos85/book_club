package hu.bmiklos.bc.service.mapper;

import java.time.Instant;
import java.util.List;

import org.springframework.lang.Nullable;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.Participant;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class EventMapper {

    private EventMapper() {
    }

    public static CreateEventDto mapToDto(Event lastEvent) {
        return new CreateEventDto(lastEvent.getBookId(), lastEvent.getTime(), lastEvent.getHostId());
    }

    public static GetEventDto mapToGetDto(Event event) {
        Instant time = event.getTime();
        BookDto book = BookMapper.mapToDto(event.getBook());
        UserDto host = mapHostByIdOrExternalId(event);
        List<UserDto> participants = event.getParticipants()
                .stream()
                .map(participant -> UserMapper.mapToDto(participant.getUser(), participant.getParticipantExternalId()))
                .toList();
        return new GetEventDto(time, book, host, event.getHostExternalId(), participants);
    }

    @Nullable
    private static UserDto mapHostByIdOrExternalId(Event event) {
        UserDto host = null;
        User hostByHostId = event.getHostByHostId();
        if (hostByHostId != null) {
            host = UserMapper.mapToDto(hostByHostId);
        }
        User hostByHostExternalId = event.getHostByHostExternalId();
        if (hostByHostExternalId != null) {
            host = UserMapper.mapToDto(hostByHostExternalId, event.getHostExternalId());
        }
        return host;
    }
}
