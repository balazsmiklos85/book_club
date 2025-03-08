package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.User;
import java.util.Collection;

public interface UserRepository {
  Collection<User> findAll();
}
