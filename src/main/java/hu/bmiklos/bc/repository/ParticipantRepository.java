package hu.bmiklos.bc.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Participant;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    Optional<Participant> findByEventIdAndParticipantExternalId(UUID eventId, int participantExternalId);

}