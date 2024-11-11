package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.business.mapper.UserEntitiesMapper;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.repository.UserJpaRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBusinessRepository implements UserRepository {
  private final UserJpaRepository userJpaRepository;

  @Override
  public Collection<User> findAll() {
    UserEntitiesMapper mapper = new UserEntitiesMapper();
    return mapper.convert(userJpaRepository.findAll());
  }
}
