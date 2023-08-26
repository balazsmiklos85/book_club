package hu.bmiklos.bc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.UserPassword;

public interface PasswordRepository extends JpaRepository<UserPassword, UUID> { }
