package hu.bmiklos.bc.model.repository;

import hu.bmiklos.bc.model.entity.BookEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<BookEntity, UUID> {}
