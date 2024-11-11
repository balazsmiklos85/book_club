package hu.bmiklos.bc.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionEntity {
    @Id
    private UUID id;
    private UUID bookId;
    private UUID userId;
    private Instant creationDate;
    private String description;

    @OneToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserEntity suggester;

    @OneToOne
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private BookEntity book;

    public SuggestionEntity(UUID bookId, UUID userId, Instant creationDate, String description) {
        this.id = UUID.randomUUID();
        this.bookId = bookId;
        this.userId = userId;
        this.creationDate = creationDate;
        this.description = description;
    }
}
