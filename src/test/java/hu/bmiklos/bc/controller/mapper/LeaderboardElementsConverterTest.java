package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

class LeaderboardElementsConverterTest {
    @Test
    void mapsBooks() {
        var converter = new LeaderboardElementsConverter(List.of(), Map.of());
        var bookId = UUID.randomUUID();
        var book = new BookDto(bookId, "Test Author", "Test Title", "test://");
        Set<SuggestionDto> suggestion = Set.of();
        var bookAndSuggestion = new BookAndSuggesterDto(book, suggestion);
        Collection<BookAndSuggesterDto> books = List.of(bookAndSuggestion);

        LeaderboardBookData result = converter.convert(books)
            .iterator()
            .next();

        assertEquals(bookId, result.id(), "The books should have been mapped.");
    }
}

