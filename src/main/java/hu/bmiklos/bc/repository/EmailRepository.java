package hu.bmiklos.bc.repository;

import hu.bmiklos.bc.model.Email;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, String> {
  Integer countByUserId(UUID userId);
}
