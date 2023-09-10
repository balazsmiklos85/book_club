package hu.bmiklos.bc.controller.mapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import hu.bmiklos.bc.controller.dto.CreateEventRequest;
import hu.bmiklos.bc.controller.dto.EventData;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class EventMapper {

    private EventMapper() {}

    public static CreateEventDto mapToDto(CreateEventRequest event) {
        UUID mappedBookId = UUID.fromString(event.getBookId());
        Instant mappedTime = DateTimeMapper.toInstant(event.getDate(), event.getTime());
        UUID mappedHostId = UUID.fromString(event.getHost());

        return new CreateEventDto(mappedBookId, mappedTime, mappedHostId);
    }

    public static List<EventData> mapToEventData(List<GetEventDto> eventDtos, UUID currentUser) {
        return eventDtos.stream()
            .map(event -> mapToEventData(event, currentUser))
            .collect(Collectors.toList());
    }

    private static EventData mapToEventData(GetEventDto eventDto, UUID currentUser) {
        String date = DateTimeMapper.toLocalDateString(eventDto.getTime());
        String author = eventDto.getBook().getAuthor();
        String title = eventDto.getBook().getTitle();    
        String hostName = getHostNameOrExternalId(eventDto);
        boolean userAttended = eventDto.getParticipants().contains(eventDto.getHost());
        boolean userHosted = Optional.ofNullable(eventDto.getHost())
            .map(UserDto::getId)
            .map(id -> id.equals(currentUser))
            .orElse(false);
        return new EventData(date, author, title, hostName, userAttended, userHosted);
    }

    private static String getHostNameOrExternalId(GetEventDto eventDto) {
        return Optional.ofNullable(eventDto.getHost())
            .map(UserDto::getName)
            .orElse(getHostExternalId(eventDto));
    }

    private static String getHostExternalId(GetEventDto eventDto) {
        String hostExternalId = Optional.ofNullable(eventDto.getHostExternalId())
            .map(Object::toString)
            .orElse("N/A");
        return "[" + hostExternalId + "]";
    }
}
