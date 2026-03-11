package hu.bmiklos.bc.repository;

import hu.bmiklos.bc.model.Suggestion;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {

  void deleteByBookId(UUID bookId);
}
