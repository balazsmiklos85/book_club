package hu.bmiklos.bc.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public class ParticipantId implements Serializable {
    private static final long serialVersionUID = -2456832834651093519L;

    private UUID eventId;   
    private int participantExternalId;

    public ParticipantId() {
    }

    public ParticipantId(UUID eventId, int participantExternalId) {
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

    @Override
    public int hashCode() {
        return Objects.hash(eventId, participantExternalId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ParticipantId))
            return false;
        ParticipantId other = (ParticipantId) obj;
        return Objects.equals(eventId, other.eventId) && participantExternalId == other.participantExternalId;
    }    
}
