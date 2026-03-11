package hu.bmiklos.bc.repository;

import hu.bmiklos.bc.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @deprecated Use {@link UserBusinessRepository} in the business layer, and {@link
 *     UserJpaRepository} in the JPA adapter.
 */
@Deprecated
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByExternalId(int externalId);

  List<User> findByNameLike(String string);
}
