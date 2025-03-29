package hu.bmiklos.bc.controller.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.web.dto.LeaderboardBookData;
import hu.bmiklos.bc.web.dto.SuggestionReference;
import hu.bmiklos.bc.web.mapper.LeaderboardElementConverter;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LeaderboardElementConverterTest {
  @Test
  void mapsBookFields() {
    var user = new User(null, null, null, null);
    var converter = new LeaderboardElementConverter(user);
    var bookId = UUID.randomUUID();
    var book =
        new Book(
            bookId, "Test Author", "Test Title", "test://url.hu", ignored -> null, ignored -> null);

    LeaderboardBookData result = converter.convert(book);

    assertEquals(bookId, result.id(), "The book ID should have been mapped.");
    assertEquals("Test Author", result.author(), "The author should have been mapped.");
    assertEquals("Test Title", result.title(), "The title should have been mapped.");
    assertEquals("test://url.hu", result.url(), "The URL should have been mapped.");
  }

  @Test
  void mapsSuggestions() {

    var user = new User(null, null, null, null);
    var converter = new LeaderboardElementConverter(user);
    var bookId = UUID.randomUUID();
    var suggestionId = UUID.randomUUID();
    var suggester = new ShallowUser(UUID.randomUUID(), "Test User", false, -1, List.of());
    var suggestion =
        new Suggestion(
            suggestionId, Instant.now(), "Test description", suggester, mock(Book.class));
    var book =
        new Book(
            bookId,
            "Test Author",
            "Test Title",
            "test://url.hu",
            b -> List.of(suggestion),
            ignored -> null);

    SuggestionReference result = converter.convert(book).suggestions().iterator().next();

    assertEquals(suggestionId, result.getId(), "The suggestion ID should have been mapped.");
    assertEquals("Test User", result.getName(), "The suggester name should have been mapped.");
  }

  @Test
  void mapsVoters() {
    var bookId = UUID.randomUUID();
    var voter =
        new ShallowUser(
            UUID.randomUUID(), "Test User", false, -1, List.of(new Email("user@test.hu")));
    var user = new User(null, null, null, null);
    var converter = new LeaderboardElementConverter(user);
    var vote = new Vote(UUID.randomUUID(), mock(Book.class), voter);
    var book =
        new Book(
            bookId,
            "Test Author",
            "Test Title",
            "test://url.hu",
            ignored -> null,
            b -> List.of(vote));

    String result = converter.convert(book).voterHashes().iterator().next();

    assertEquals(
        "f88054c4794146f676dc7d326d8cdd917d17782b625996a4c1c37a1211900427",
        result,
        "The voter should have been mapped.");
  }

  @Test
  void mapsUserVote() {
    var bookId = UUID.randomUUID();
    var voterId = 1234;
    var user = new User(null, null, null, voterId);
    var converter = new LeaderboardElementConverter(user);
    var voter = new ShallowUser(null, "Test User", false, voterId, List.of());
    var vote = new Vote(UUID.randomUUID(), mock(Book.class), voter);
    var book =
        new Book(
            bookId,
            "Test Author",
            "Test Title",
            "test://url.hu",
            ignored -> null,
            b -> List.of(vote));

    LeaderboardBookData result = converter.convert(book);

    assertTrue(result.userVoted(), "The vote of the user should have been mapped.");
  }

  @Test
  void mapsNewSuggestionFlag() {
    var user = new User(null, null, null, null);
    var converter = new LeaderboardElementConverter(user);
    var bookId = UUID.randomUUID();
    var suggester = new ShallowUser(UUID.randomUUID(), "Test User", false, -1, List.of());
    var suggestionId = UUID.randomUUID();
    var now = Instant.now();
    var suggestion = new Suggestion(suggestionId, now, null, suggester, mock(Book.class));
    var book =
        new Book(
            bookId,
            "Test Author",
            "Test Title",
            "test://url.hu",
            b -> List.of(suggestion),
            ignored -> null);

    LeaderboardBookData result = converter.convert(book);

    assertTrue(result.isNew(), "The 'new' flag should have been mapped.");
  }
}
