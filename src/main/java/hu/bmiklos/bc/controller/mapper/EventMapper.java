package hu.bmiklos.bc.controller.mapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import hu.bmiklos.bc.controller.dto.BookData;
import hu.bmiklos.bc.controller.dto.CreateEventRequest;
import hu.bmiklos.bc.controller.dto.EventData;
import hu.bmiklos.bc.controller.dto.HostData;
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

    public static EventData mapToEventData(GetEventDto eventDto, UUID currentUser) {
        String date = DateTimeMapper.toLocalDateString(eventDto.getTime());
        String time = DateTimeMapper.toLocalTimeString(eventDto.getTime());
        String bookId = eventDto.getBook().getId().toString();
        String author = eventDto.getBook().getAuthor();
        String title = eventDto.getBook().getTitle();
        HostData host = UserMapper.mapToHostData(eventDto, currentUser);
        boolean userAttended = eventDto.getParticipants().contains(eventDto.getHost());

        return new EventData(eventDto.getId().toString(), date, time, new BookData(bookId, title, author), host, userAttended);
    }
}
