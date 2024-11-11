package hu.bmiklos.bc.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "books")
@NamedEntityGraph(name = "BookEntity.recommenderInfo", attributeNodes = @NamedAttributeNode("recommender"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String url;

    /**
     * @deprecated Use {@link SuggestionEntity} to get information about the recommender.
     */
    @Deprecated
    @Column(nullable = true)
    private Integer recommenderExternalId;

    /**
     * @deprecated Use {@link SuggestionEntity} to get information about the recommender.
     */
    @Deprecated
    @Column(nullable = true)
    private Instant recommendedAt;

    @OneToMany
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private List<EventEntity> events;

    /**
     * @deprecated Use {@link SuggestionEntity} to get information about the recommender.
     */
    @Deprecated
    @OneToOne
    @JoinColumn(name = "recommenderExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    @Nullable
    private UserEntity recommender;

    @OneToMany
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    @Nullable
    private Set<SuggestionEntity> suggestions;

    /**
     * @deprecated Use {@link SuggestionEntity} to store information about who suggested the book.
     */
    @Deprecated
    public BookEntity(String author, String title, String url, Integer recommenderExternalId, Instant recommendedAt) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommenderExternalId = recommenderExternalId;
        this.recommendedAt = recommendedAt;
    }

    public BookEntity(String author, String title, String url) {
        this(author, title, url, null, null);
    }
}
