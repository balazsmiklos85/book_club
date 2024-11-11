package hu.bmiklos.bc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participants")
@IdClass(ParticipantId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {

    @Id
    @Column(nullable = false)
    private UUID eventId;

    @Id
    @Column(nullable = false)
    private int participantExternalId;

    @OneToOne
    @JoinColumn(name = "participantExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private UserEntity user;

    public ParticipantEntity(UUID eventId, int participantExternalId) {
        this.eventId = eventId;
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
        if (!(obj instanceof ParticipantEntity))
            return false;
        ParticipantEntity other = (ParticipantEntity) obj;
        return Objects.equals(eventId, other.eventId) && participantExternalId == other.participantExternalId;
    }
}
