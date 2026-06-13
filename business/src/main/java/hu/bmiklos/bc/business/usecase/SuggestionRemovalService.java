package hu.bmiklos.bc.business.usecase;

import static java.util.Objects.isNull;

import hu.bmiklos.bc.business.exception.DifferentUserException;
import hu.bmiklos.bc.business.repository.BookRepository;
import hu.bmiklos.bc.business.repository.SuggestionRepository;
import hu.bmiklos.bc.domain.entities.Member;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestionRemovalService {
  private final SuggestionRepository suggestionRepository;
  private final BookRepository bookRepository;

  public void removeSuggestion(User user, UUID suggestionId) {
    suggestionRepository
        .findById(suggestionId)
        .ifPresentOrElse(
            suggestion -> deleteSuggestion(user, suggestion),
            () -> bookRepository.clearLegacyRecommender(suggestionId));
  }

  private void deleteSuggestion(User user, Suggestion suggestion) {
    Member suggester = suggestion.getSuggester();
    if (isNull(suggester) || !suggester.isSameAs(user)) {
      throw new DifferentUserException();
    }
    suggestionRepository.delete(suggestion);
  }
}
