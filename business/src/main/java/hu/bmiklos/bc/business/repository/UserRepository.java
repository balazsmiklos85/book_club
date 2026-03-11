package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.User;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Collection<User> findAll();

  Optional<User> findById(UUID id);
}
