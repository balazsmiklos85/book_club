package hu.bmiklos.bc.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

    Optional<Event> findTopByOrderByTimeDesc();
}
