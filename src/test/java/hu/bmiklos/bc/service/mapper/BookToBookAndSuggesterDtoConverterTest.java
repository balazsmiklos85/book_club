package hu.bmiklos.bc.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

class BookToBookAndSuggesterDtoConverterTest {
    private BookToBookAndSuggesterDtoConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new BookToBookAndSuggesterDtoConverter();
    }

    @Test
    void convertsUnregisteredSuggester() {
        var book = new Book();
        book.setRecommenderExternalId(-1);

        BookAndSuggesterDto result = converter.convert(book);

        Set<SuggestionDto> suggestions = result.getSuggesters();
        assertEquals(1, suggestions.size(), "There should be just the historic suggester.");
        suggestions.stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getExternalId)
            .forEach(externalId -> assertEquals(-1, externalId, "The historic suggester should have been converted."));
    }

    @Test
    void convertsRegisteredHistoricSuggester() {
        var book = new Book();
        var historicSuggester = new User();
        var suggesterId = UUID.randomUUID();
        historicSuggester.setId(suggesterId);
        book.setRecommender(historicSuggester);

        BookAndSuggesterDto result = converter.convert(book);

        Set<SuggestionDto> suggestions = result.getSuggesters();
        assertEquals(1, suggestions.size(), "There should be just the historic suggester.");
        suggestions.stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getId)
            .forEach(id -> assertEquals(suggesterId, id, "The historic suggester should have been converted."));
    }

    @Test
    void convertsSuggester() {
        var book = new Book();
        var historicSuggester = new User();
        var suggesterId = UUID.randomUUID();
        historicSuggester.setId(suggesterId);
        var suggestion = mock(Suggestion.class);
        var suggester = new User();
        suggester.setId(suggesterId);
        when(suggestion.getSuggester()).thenReturn(suggester);
        book.setSuggestions(Set.of(suggestion));

        BookAndSuggesterDto result = converter.convert(book);

        Set<SuggestionDto> suggestions = result.getSuggesters();
        assertEquals(1, suggestions.size(), "There should be just the suggester.");
        suggestions.stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getId)
            .forEach(id -> assertEquals(suggesterId, id, "The suggester should have been converted."));
    }

    @Test
    void convertsSuggesterOverridesHistoricSuggester() {
        var book = new Book();
        book.setRecommenderExternalId(-2);
        var historicSuggester = new User();
        var suggesterId = UUID.randomUUID();
        historicSuggester.setId(suggesterId);
        var suggestion = mock(Suggestion.class);
        var suggester = new User();
        suggester.setId(suggesterId);
        suggester.setExternalId(-2);
        when(suggestion.getSuggester()).thenReturn(suggester);
        book.setSuggestions(Set.of(suggestion));

        BookAndSuggesterDto result = converter.convert(book);

        Set<SuggestionDto> suggestions = result.getSuggesters();
        assertEquals(1, suggestions.size(), "There should be just the suggester.");
        suggestions.stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getId)
            .forEach(id -> assertEquals(suggesterId, id, "The suggester should have been converted."));
    }

    @Test
    void convertsSuggesterOverridesRegisteredHistoricSuggester() {
        var book = new Book();
        var historicSuggester = new User();
        var suggesterId = UUID.randomUUID();
        historicSuggester.setId(suggesterId);
        var suggestion = mock(Suggestion.class);
        var suggester = new User();
        suggester.setId(suggesterId);
        suggester.setExternalId(-2);
        when(suggestion.getSuggester()).thenReturn(suggester);
        book.setSuggestions(Set.of(suggestion));
        book.setRecommender(suggester);

        BookAndSuggesterDto result = converter.convert(book);

        Set<SuggestionDto> suggestions = result.getSuggesters();
        assertEquals(1, suggestions.size(), "There should be just the suggester.");
        suggestions.stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getId)
            .forEach(id -> assertEquals(suggesterId, id, "The suggester should have been converted."));
    }
}

