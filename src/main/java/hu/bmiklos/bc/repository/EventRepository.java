package hu.bmiklos.bc.repository;

import hu.bmiklos.bc.model.Event;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {

  Optional<Event> findTopByOrderByTimeDesc();
}
