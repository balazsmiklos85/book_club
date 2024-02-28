package hu.bmiklos.bc.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

class SuggestionDtoToSuggestionFormDataConverterTest {

    private SuggestionDtoToSuggestionFormDataConverter converter;
    private UUID targetSuggestionId;

    @BeforeEach
    void setup() {
        this.targetSuggestionId = UUID.randomUUID();
        this.converter = new SuggestionDtoToSuggestionFormDataConverter(targetSuggestionId);
    }

    @Test
    void doesNotFindSuggestionsFromOthers() {
        var suggestions = new LinkedHashSet<SuggestionDto>(101);
        for (int i = 0; i < 100; i++) {
            var suggestion = mock(SuggestionDto.class);
            when(suggestion.getId()).thenReturn(UUID.randomUUID());
            suggestions.add(suggestion);
        }
        suggestions.add(new SuggestionDto(targetSuggestionId, Instant.now(), mock(UserDto.class), "Test description"));
        var bookAndSuggestions = new BookAndSuggesterDto(
                new BookDto(UUID.randomUUID(), "Test Author", "Test Title",
                    "http://test.url/for/book"),
                suggestions);

        var result = converter.convert(bookAndSuggestions);

        assertEquals("Test description", result.getDescription(), "The description should be found by the user ID.");
    }
}

