package hu.bmiklos.bc.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SuggestionReferenceConverterTest {

  @Test
  void shouldUseBookIdWhenSuggestionIdIsNull() {
    UUID bookId = givenABookId();
    Suggestion suggestion = givenALegacySuggestionWithNullId(bookId);
    SuggestionReferenceConverter converter = givenAConverter();

    var result = converter.convert(suggestion);

    assertEquals(
        bookId,
        result.getId(),
        "The suggestion reference ID should fall back to the book ID when suggestion ID is null.");
  }

  private UUID givenABookId() {
    return UUID.randomUUID();
  }

  private Suggestion givenALegacySuggestionWithNullId(UUID bookId) {
    var suggester = new ShallowUser(UUID.randomUUID(), "Test User", false, -1, List.of());
    var book = mock(Book.class);
    when(book.getId()).thenReturn(bookId);
    return new Suggestion(null, Instant.now(), "Test description", suggester, book);
  }

  private SuggestionReferenceConverter givenAConverter() {
    var caller = new User(UUID.randomUUID(), "Caller", false, -1);
    return new SuggestionReferenceConverter(caller);
  }
}
