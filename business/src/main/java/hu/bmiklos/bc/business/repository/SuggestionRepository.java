package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Suggestion;
import java.util.Optional;
import java.util.UUID;

public interface SuggestionRepository {

  Optional<Suggestion> findById(UUID suggestionId);

  void delete(Suggestion suggestion);
}
