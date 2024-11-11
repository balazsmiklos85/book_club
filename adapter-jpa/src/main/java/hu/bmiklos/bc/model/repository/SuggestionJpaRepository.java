package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.model.entity.SuggestionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestionJpaRepository extends JpaRepository<SuggestionEntity, UUID> {
  Optional<SuggestionEntity> findTopByBookIdOrderByCreationDate(UUID bookId);
}
