package hu.bmiklos.bc.business.usecase;

import static java.util.Objects.isNull;

import hu.bmiklos.bc.business.exception.DifferentUserException;
import hu.bmiklos.bc.business.exception.EntityNotFoundException;
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

  public void removeSuggestion(User user, UUID suggestionId) {
    Suggestion suggestion =
        suggestionRepository
            .findById(suggestionId)
            .orElseThrow(() -> new EntityNotFoundException(suggestionId));
    Member suggester = suggestion.getSuggester();
    if (isNull(suggester) || !suggester.isSameAs(user)) {
      throw new DifferentUserException();
    }
    suggestionRepository.delete(suggestion);
  }
}
