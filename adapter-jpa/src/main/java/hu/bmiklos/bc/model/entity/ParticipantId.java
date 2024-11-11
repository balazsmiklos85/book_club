package hu.bmiklos.bc.model.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantId implements Serializable {
    private static final long serialVersionUID = -2456832834651093519L;

    private UUID eventId;   
    private int participantExternalId;

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
