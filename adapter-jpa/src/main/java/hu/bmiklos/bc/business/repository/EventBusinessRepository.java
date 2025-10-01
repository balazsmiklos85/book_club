package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Event;
import hu.bmiklos.bc.model.repository.EventJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventBusinessRepository implements EventRepository {
  private final EventJpaRepository eventJpaRepository;

  @Override
  public Optional<Event> findTopByOrderByTimeDesc() {
    return eventJpaRepository.findTopByOrderByTimeDesc();
  }
}
