package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

class LeaderboardElementConverterTest {
    @Test
    void mapsBookFields() {
        var converter = new LeaderboardElementConverter(List.of(), Map.of());
        var bookId = UUID.randomUUID();
        BookDto book = new BookDto(bookId, "Test Author", "Test Title",
                "test://url.hu");
        var source = new BookAndSuggesterDto(book, Set.of());

        LeaderboardBookData result = converter.convert(source);

        assertEquals(bookId, result.id(),
                "The book ID should have been mapped.");
        assertEquals("Test Author", result.author(),
                "The author should have been mapped.");
        assertEquals("Test Title", result.title(),
                "The title should have been mapped.");
        assertEquals("test://url.hu", result.url(),
                "The URL should have been mapped.");
    }

    @Test
    void mapsSuggestions() {
        var converter = new LeaderboardElementConverter(List.of(), Map.of());
        var bookId = UUID.randomUUID();
        BookDto book = new BookDto(bookId, "Test Author", "Test Title",
                "test://url.hu");
        var suggester = new UserDto(UUID.randomUUID(), "Test User", -1);
        var suggestionId = UUID.randomUUID();
        var now = Instant.now();
        var suggestion = new SuggestionDto(suggestionId, now, suggester,
                "Test description");
        var source = new BookAndSuggesterDto(book, Set.of(suggestion));

        SuggestionReference result = converter.convert(source)
            .suggestions()
            .iterator()
            .next();

        assertEquals(suggestionId, result.getId(),
                "The suggestion ID should have been mapped.");
        assertEquals("Test User", result.getName(),
                "The suggester name should have been mapped.");
    }

    @Test
    void mapsVoters() {
        var bookId = UUID.randomUUID();
        var voter = new UserDto(UUID.randomUUID(), "Test User", -1,
                List.of("user@test.hu"));
        var converter = new LeaderboardElementConverter(List.of(),
                Map.of(bookId, List.of(voter)));
        BookDto book = new BookDto(bookId, "Test Author", "Test Title",
                "test://url.hu");
        var source = new BookAndSuggesterDto(book, Set.of());

        String result = converter.convert(source)
            .voterHashes()
            .iterator()
            .next();

        assertEquals(
                "f88054c4794146f676dc7d326d8cdd917d17782b625996a4c1c37a1211900427",
                result, "The voter should have been mapped.");
    }

    @Test
    void mapsUserVote() {
        var bookId = UUID.randomUUID();
        var converter = new LeaderboardElementConverter(List.of(bookId),
                Map.of());
        BookDto book = new BookDto(bookId, "Test Author", "Test Title",
                "test://url.hu");
        var source = new BookAndSuggesterDto(book, Set.of());

        LeaderboardBookData result = converter.convert(source);

        assertTrue(result.userVoted(),
                "The vote of the user should have been mapped.");
    }

    @Test
    void mapsNewSuggestionFlag() {
        var converter = new LeaderboardElementConverter(List.of(), Map.of());
        var bookId = UUID.randomUUID();
        BookDto book = new BookDto(bookId, "Test Author", "Test Title",
                "test://url.hu");
        var suggester = new UserDto(UUID.randomUUID(), "Test User", -1);
        var suggestionId = UUID.randomUUID();
        var now = Instant.now();
        var suggestion = new SuggestionDto(suggestionId, now, suggester,
                "Test description");
        var source = new BookAndSuggesterDto(book, Set.of(suggestion));

        LeaderboardBookData result = converter.convert(source);

        assertTrue(result.isNew(), "The 'new' flag should have been mapped.");
    }
}

