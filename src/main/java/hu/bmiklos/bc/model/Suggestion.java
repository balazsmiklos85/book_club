package hu.bmiklos.bc.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "suggestions")
@Deprecated
public class Suggestion {
    @Id
    private UUID id;
    private UUID bookId;
    private UUID userId;
    private Instant creationDate;
    private String description;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private User suggester;

    @OneToOne
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private Book book;

    public Suggestion() {}

    public Suggestion(UUID bookId, UUID userId, Instant creationDate, String description) {
        this.id = UUID.randomUUID();
        this.bookId = bookId;
        this.userId = userId;
        this.creationDate = creationDate;
        this.description = description;
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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getSuggester() {
        return suggester;
    }

    public Book getBook() {
        return book;
    }
}
