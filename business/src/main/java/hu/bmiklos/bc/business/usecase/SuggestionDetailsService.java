package hu.bmiklos.bc.business.usecase;

import hu.bmiklos.bc.domain.entities.Suggestion;
import java.util.Optional;
import java.util.UUID;

public interface SuggestionDetailsService {
  Optional<Suggestion> findOldestByBookId(UUID bookId);
}
