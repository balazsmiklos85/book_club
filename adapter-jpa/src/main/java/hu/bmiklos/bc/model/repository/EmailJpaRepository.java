package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.model.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailJpaRepository extends JpaRepository<EmailEntity, String> {

}
