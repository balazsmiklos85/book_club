package hu.bmiklos.bc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {

    void deleteByBookId(UUID bookId);
}

