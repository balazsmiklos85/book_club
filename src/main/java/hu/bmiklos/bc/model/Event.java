package hu.bmiklos.bc.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
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

    public Event() {}

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
}