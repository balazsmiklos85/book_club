package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.business.mapper.SuggestionEntityMapper;
import hu.bmiklos.bc.business.usecase.SuggestionDetailsService;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.model.repository.SuggestionJpaRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuggestionBusinessRepository implements SuggestionDetailsService {

  private final SuggestionJpaRepository suggestionRepository;

  @Override
  public Optional<Suggestion> findOldestByBookId(UUID bookId) {
    return suggestionRepository
        .findTopByBookIdOrderByCreationDate(bookId)
        .map(suggestion -> new SuggestionEntityMapper().convert(suggestion));
  }
}
