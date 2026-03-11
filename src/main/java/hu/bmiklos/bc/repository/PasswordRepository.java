package hu.bmiklos.bc.repository;

import hu.bmiklos.bc.model.Password;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordRepository extends JpaRepository<Password, UUID> {}
