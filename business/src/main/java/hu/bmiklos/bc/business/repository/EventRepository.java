package hu.bmiklos.bc.business.repository;

import java.time.Instant;
import java.util.Optional;

public interface EventRepository {
  Optional<Instant> findLastEventTime();
}
