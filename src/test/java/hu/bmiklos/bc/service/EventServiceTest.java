package hu.bmiklos.bc.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.bmiklos.bc.repository.EventRepository;
import hu.bmiklos.bc.service.dto.CreateEventDto;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private SuggestionService suggestionService;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setup() {
        when(eventRepository.saveAndFlush(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void eventCreationRemovesSuggestions() {
        var bookId = UUID.randomUUID();
        var event = new CreateEventDto(bookId, Instant.now(), UUID.randomUUID());

        eventService.createEvent(event);

        verify(suggestionService, times(1)).removeForBook(bookId);
    }
}

