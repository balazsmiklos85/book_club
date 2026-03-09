package hu.bmiklos.bc.business.usecase;

import hu.bmiklos.bc.business.repository.EventRepository;
import hu.bmiklos.bc.business.repository.UserRepository;
import hu.bmiklos.bc.business.security.ActiveUserService;
import hu.bmiklos.bc.domain.entities.Event;
import hu.bmiklos.bc.domain.entities.User;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventCreationService {
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final ActiveUserService activeUserService;

  public Optional<Event> findLastEvent() {
    return eventRepository.findTopByOrderByTimeDesc();
  }

  public Collection<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getCurrentUser() {
    return activeUserService.getUser();
  }
}
