package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.model.repository.EventJpaRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventBusinessRepository implements EventRepository {
  private final EventJpaRepository eventJpaRepository;

  @Override
  public Optional<Instant> findLastEventTime() {
    return eventJpaRepository.findTopByOrderByTimeDesc().map(e -> e.getTime());
  }
}
