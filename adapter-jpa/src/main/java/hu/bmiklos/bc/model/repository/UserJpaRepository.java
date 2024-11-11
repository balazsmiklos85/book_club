package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.model.entity.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {}
