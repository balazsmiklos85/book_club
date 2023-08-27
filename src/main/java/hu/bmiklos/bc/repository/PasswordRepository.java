package hu.bmiklos.bc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Password;

public interface PasswordRepository extends JpaRepository<Password, UUID> { }
