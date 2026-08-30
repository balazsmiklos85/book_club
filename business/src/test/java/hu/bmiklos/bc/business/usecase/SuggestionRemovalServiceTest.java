package hu.bmiklos.bc.business.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hu.bmiklos.bc.business.exception.DifferentUserException;
import hu.bmiklos.bc.business.exception.EntityNotFoundException;
import hu.bmiklos.bc.business.repository.BookRepository;
import hu.bmiklos.bc.business.repository.SuggestionRepository;
import hu.bmiklos.bc.domain.entities.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestionRemovalServiceTest {

  private static final String OTHER_USER = "Other Test User";
  private static final String USER_NAME = "Test User";

  @Mock private BookRepository bookRepository;

  @Mock private SuggestionRepository suggestionRepository;

  @InjectMocks private SuggestionRemovalService suggestionRemovalService;

  @Test
  void removeSuggestionShouldDeleteSuggestionWhenUserIsSuggester() {
    var suggestionId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var suggester = new ShallowUser(userId, USER_NAME, false, -1, List.of());
    var book =
        new Book(
            UUID.randomUUID(),
            "Test Author",
            "Test Title",
            "test://url.hu",
            ignored -> null,
            ignored -> null);
    var suggestion =
        new Suggestion(suggestionId, Instant.now(), "Test suggestion", suggester, book);
    var user = new User(userId, USER_NAME, false, -1);

    when(suggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));

    suggestionRemovalService.removeSuggestion(user, suggestionId);

    verify(suggestionRepository).delete(suggestion);
  }

  @Test
  void removeSuggestionShouldThrowEntityNotFoundExceptionWhenSuggestionNotFound() {
    var suggestionId = UUID.randomUUID();
    var user = new User(UUID.randomUUID(), USER_NAME, false, -1);

    when(suggestionRepository.findById(suggestionId)).thenReturn(Optional.empty());
    doThrow(new EntityNotFoundException(suggestionId))
        .when(bookRepository)
        .clearLegacyRecommender(suggestionId);

    assertThatThrownBy(() -> suggestionRemovalService.removeSuggestion(user, suggestionId))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining(suggestionId.toString());

    verify(suggestionRepository, never()).delete(any());
  }

  @Test
  void removeSuggestionShouldThrowDifferentUserExceptionWhenSuggesterIsNull() {
    var suggestionId = UUID.randomUUID();
    var user = new User(UUID.randomUUID(), USER_NAME, false, -1);
    var book =
        new Book(
            UUID.randomUUID(),
            "Test Author",
            "Test Title",
            "test://url.hu",
            ignored -> null,
            ignored -> null);
    var suggestion = new Suggestion(suggestionId, Instant.now(), "Test suggestion", null, book);

    when(suggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));

    assertThatThrownBy(() -> suggestionRemovalService.removeSuggestion(user, suggestionId))
        .isInstanceOf(DifferentUserException.class);

    verify(suggestionRepository, never()).delete(any());
  }

  @Test
  void removeSuggestionShouldThrowDifferentUserExceptionWhenUserIsNotSuggester() {
    var suggestionId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var otherUserId = UUID.randomUUID();
    var suggester = new ShallowUser(userId, "Suggestion from another user", false, -2, List.of());
    var book =
        new Book(
            UUID.randomUUID(),
            "Test Author",
            "Test Title",
            "test://url.hu",
            ignored -> null,
            ignored -> null);
    var suggestion =
        new Suggestion(suggestionId, Instant.now(), "Test suggestion", suggester, book);
    var user = new User(otherUserId, OTHER_USER, false, -1);

    when(suggestionRepository.findById(suggestionId)).thenReturn(Optional.of(suggestion));

    assertThatThrownBy(() -> suggestionRemovalService.removeSuggestion(user, suggestionId))
        .isInstanceOf(DifferentUserException.class);

    verify(suggestionRepository, never()).delete(any());
  }

  @Test
  void removeSuggestion_deletesLegacySuggestionWhenIdIsBookId() {
    var bookId = givenABookWithLegacyRecommender();
    var user = givenAUser();

    suggestionRemovalService.removeSuggestion(user, bookId);

    thenLegacyRecommenderWasCleared(bookId);
  }

  private UUID givenABookWithLegacyRecommender() {
    var bookId = UUID.randomUUID();

    when(suggestionRepository.findById(bookId)).thenReturn(Optional.empty());

    return bookId;
  }

  private User givenAUser() {
    return new User(UUID.randomUUID(), USER_NAME, false, -1);
  }

  private void thenLegacyRecommenderWasCleared(UUID bookId) {
    verify(bookRepository).clearLegacyRecommender(bookId);
  }
}
