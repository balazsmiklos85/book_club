package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Email;
import java.util.Optional;

public interface EmailRepository {
  Optional<Email> findById(String username);
}
