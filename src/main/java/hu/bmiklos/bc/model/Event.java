package hu.bmiklos.bc.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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

@Entity
@Table(name = "events")
@Deprecated
public class Event {
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
    private Book book;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostId", referencedColumnName = "id", insertable = false, updatable = false)
    private User hostByHostId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hostExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    private User hostByHostExternalId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventId", referencedColumnName = "id", insertable = false, updatable = false)
    private List<Participant> participants;

    public Event() {
    }

    public Event(UUID bookId, Instant time, UUID hostId) {
        this.bookId = bookId;
        this.time = time;
        this.hostId = hostId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Integer getHostExternalId() {
        return hostExternalId;
    }

    public void setHostExternalId(Integer hostExternalId) {
        this.hostExternalId = hostExternalId;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }    

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getHostByHostId() {
        return hostByHostId;
    }

    public void setHostByHostId(User hostByHostId) {
        this.hostByHostId = hostByHostId;
    }

    public User getHostByHostExternalId() {
        return hostByHostExternalId;
    }

    public void setHostByHostExternalId(User hostByHostExternalId) {
        this.hostByHostExternalId = hostByHostExternalId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, time, hostExternalId, hostId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Event))
            return false;
        Event other = (Event) obj;
        return Objects.equals(id, other.id)
                && Objects.equals(bookId, other.bookId)
                && Objects.equals(time, other.time)
                && Objects.equals(hostExternalId, other.hostExternalId)
                && Objects.equals(hostId, other.hostId);
    }
}