package hu.bmiklos.bc.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Participant;
import hu.bmiklos.bc.model.ParticipantId;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

    Optional<Participant> findByEventIdAndParticipantExternalId(UUID eventId, int participantExternalId);

}