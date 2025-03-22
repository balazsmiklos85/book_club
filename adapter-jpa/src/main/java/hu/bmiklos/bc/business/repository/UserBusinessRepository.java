package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.business.mapper.UserEntityMapper;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.repository.UserJpaRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBusinessRepository implements UserRepository {
  private final UserJpaRepository userJpaRepository;

  @Override
  public Collection<User> findAll() {
    UserEntityMapper mapper = new UserEntityMapper();
    return userJpaRepository.findAll().stream().map(mapper::convert).toList();
  }

  @Override
  public Optional<User> findById(UUID id) {
    UserEntityMapper mapper = new UserEntityMapper();
    return userJpaRepository.findById(id).map(mapper::convert);
  }
}
