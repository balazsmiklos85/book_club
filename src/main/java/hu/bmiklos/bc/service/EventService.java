package hu.bmiklos.bc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.repository.EventRepository;
import hu.bmiklos.bc.service.dto.CreateEventDto;
import hu.bmiklos.bc.service.dto.EditEventDto;
import hu.bmiklos.bc.service.dto.GetEventDto;
import hu.bmiklos.bc.service.mapper.EventMapper;
import jakarta.transaction.Transactional;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public GetEventDto createEvent(CreateEventDto event) {
        UUID hostId = event.getHostId()
            .orElseThrow(() -> new IllegalArgumentException("The host ID is required for new events."));

        var toCreate = new Event(event.getBookId(), event.getTime(), hostId);
        Event saved = eventRepository.saveAndFlush(toCreate);
        
        return EventMapper.mapToGetDto(saved);
    }

    @Transactional
    public GetEventDto editEvent(EditEventDto event) {
        Event storedEvent = eventRepository.findById(event.getId())
            .orElseThrow(() -> new IllegalArgumentException("No event found with ID " + event.getId()));
        storedEvent.setTime(event.getTime());
        storedEvent.setHostId(event.getHostId());
        Event saved = eventRepository.saveAndFlush(storedEvent);
        return EventMapper.mapToGetDto(saved);
    }

    @Transactional
    public List<GetEventDto> getEvents() {
        return eventRepository.findAll()
            .stream()
            .sorted((e1, e2) -> e2.getTime().compareTo(e1.getTime()))
            .map(EventMapper::mapToGetDto)
            .toList();
    }

    public Optional<Instant> proposeNewTime() {
        return eventRepository.findTopByOrderByTimeDesc()
            .map(Event::getTime)
            .map(lastEventTime -> lastEventTime.plus(4 * 7l, ChronoUnit.DAYS)); // java.time.temporal.UnsupportedTemporalTypeException: Unsupported unit: Weeks
    }

    public GetEventDto getEvent(UUID eventUuid) {
        return eventRepository.findById(eventUuid)
            .map(EventMapper::mapToGetDto)
            .orElseThrow(() -> new IllegalArgumentException("No event found with ID " + eventUuid));
    }
}