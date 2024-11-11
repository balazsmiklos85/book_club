package hu.bmiklos.bc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private Instant time;

    @Column
    private Integer hostExternalId;

    @Column
    private UUID hostId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private BookEntity book;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity hostByHostId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private UserEntity hostByHostExternalId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    private List<ParticipantEntity> participants;

    public EventEntity(UUID bookId, Instant time, UUID hostId) {
        this.bookId = bookId;
        this.time = time;
        this.hostId = hostId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, time, hostExternalId, hostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EventEntity))
            return false;
        EventEntity other = (EventEntity) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(bookId, other.bookId)
                && Objects.equals(time, other.time)
                && Objects.equals(hostExternalId, other.hostExternalId)
                && Objects.equals(hostId, other.hostId);
    }
}