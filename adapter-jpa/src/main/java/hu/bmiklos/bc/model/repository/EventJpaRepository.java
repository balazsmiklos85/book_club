package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.domain.entities.Event;
import hu.bmiklos.bc.model.entity.EventEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {
  Optional<Event> findTopByOrderByTimeDesc();
}
