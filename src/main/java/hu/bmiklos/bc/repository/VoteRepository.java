package hu.bmiklos.bc.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    void deleteByBookIdAndUserId(UUID bookId, UUID userId);

    void deleteByBookIdAndUserExternalId(UUID bookId, int externalUserId);

    List<Vote> findByUserId(UUID userId);

    List<Vote> findByUserExternalId(int externalUserId);

    Optional<Vote> findByBookIdAndUserExternalId(UUID bookId, int externalUserId);

    Optional<Vote> findByBookIdAndUserId(UUID bookId, UUID userId);
}
