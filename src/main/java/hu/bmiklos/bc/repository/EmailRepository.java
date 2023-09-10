package hu.bmiklos.bc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Email;

public interface EmailRepository extends JpaRepository<Email, String> {
    Integer countByUserId(UUID userId);
}
