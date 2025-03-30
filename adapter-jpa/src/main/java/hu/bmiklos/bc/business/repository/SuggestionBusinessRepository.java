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
public class SuggestionBusinessRepository implements SuggestionDetailsService, SuggestionRepository {

  private final SuggestionJpaRepository suggestionRepository;


  @Override
  public Optional<Suggestion> findById(UUID suggestionId) {
    return suggestionRepository
        .findById(suggestionId)
        .map(suggestion -> new SuggestionEntityMapper().convert(suggestion));
  }

  @Override
  public Optional<Suggestion> findOldestByBookId(UUID bookId) {
    return suggestionRepository
        .findTopByBookIdOrderByCreationDate(bookId)
        .map(suggestion -> new SuggestionEntityMapper().convert(suggestion));
  }

  @Override
  public void delete(Suggestion suggestion) {
    suggestionRepository.deleteById(suggestion.getId());
  }
}
