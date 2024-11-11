package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Event;
import java.util.Optional;

public interface EventRepository {
  Optional<Event> findTopByOrderByTimeDesc();
}
