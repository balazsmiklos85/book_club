package hu.bmiklos.bc.model;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "participants")
@IdClass(ParticipantId.class)
public class Participant {
    
    @Id
    @Column(nullable = false)
    private UUID eventId;

    @Id
    @Column(nullable = false)
    private int participantExternalId;

    @OneToOne
    @JoinColumn(name = "participantExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private User user;

    public Participant() {}

    public Participant(UUID eventId, int participantExternalId) {
        this.eventId = eventId;
        this.participantExternalId = participantExternalId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public int getParticipantExternalId() {
        return participantExternalId;
    }

    public void setParticipantExternalId(int participantExternalId) {
        this.participantExternalId = participantExternalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, participantExternalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Participant))
            return false;
        Participant other = (Participant) obj;
        return Objects.equals(eventId, other.eventId) && participantExternalId == other.participantExternalId;
    }
}
