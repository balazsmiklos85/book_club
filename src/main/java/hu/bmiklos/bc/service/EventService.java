package hu.bmiklos.bc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.repository.EventRepository;
import hu.bmiklos.bc.service.dto.EventDto;
import hu.bmiklos.bc.service.mapper.EventMapper;
import jakarta.transaction.Transactional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventDto createEvent(EventDto event) {
        UUID hostId = event.getHostId()
            .orElseThrow(() -> new IllegalArgumentException("The host ID is required for new events."));

        var toCreate = new Event(event.getBookId(), event.getTime(), hostId);
        Event saved = eventRepository.saveAndFlush(toCreate);
        
        return EventMapper.mapToDto(saved);
    }

    public Optional<Instant> proposeNewTime() {
        return eventRepository.findTopByOrderByTimeDesc()
            .map(Event::getTime)
            .map(lastEventTime -> lastEventTime.plus(4, ChronoUnit.WEEKS));
    }
}