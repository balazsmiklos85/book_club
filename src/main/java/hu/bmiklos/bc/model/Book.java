package hu.bmiklos.bc.model;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.lang.Nullable;

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

@Entity
@Table(name = "books")
@NamedEntityGraph(name = "Book.recommenderInfo", attributeNodes = @NamedAttributeNode("recommender"))
@Deprecated
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = true)
    private Integer recommenderExternalId;

    @Column(nullable = true)
    private Instant recommendedAt;

    @OneToMany
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    private List<Event> events;

    @OneToOne
    @JoinColumn(name = "recommenderExternalId", referencedColumnName = "externalId", insertable = false, updatable = false)
    @Nullable
    private User recommender;

    @OneToMany
    @JoinColumn(name = "bookId", referencedColumnName = "id", insertable = false, updatable = false)
    @Nullable
    private Set<Suggestion> suggestions;

    public Book() {}

    /**
     * @deprecated Use {@link Suggestion} to store information about who suggested the book.
     */
    @Deprecated
    public Book(String author, String title, String url, Integer recommenderExternalId, Instant recommendedAt) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommenderExternalId = recommenderExternalId;
        this.recommendedAt = recommendedAt;
    }

    public Book(String author, String title, String url) {
        this(author, title, url, null, null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @deprecated Use {@link Suggestion} to get information about the recommender.
     */
    @Deprecated
    @Nullable
    public Integer getRecommenderExternalId() {
        return recommenderExternalId;
    }

    /**
     * @deprecated Use {@link Suggestion} to store information about the recommender.
     */
    @Deprecated
    public void setRecommenderExternalId(Integer recommenderExternalId) {
        this.recommenderExternalId = recommenderExternalId;
    }

    /**
     * @deprecated Use {@link Suggestion} to get information about the recommender.
     */
    @Deprecated
    public Instant getRecommendedAt() {
        return recommendedAt;
    }

    /**
     * @deprecated Use {@link Suggestion} to store information about the recommender.
     */
    @Deprecated
    public void setRecommendedAt(Instant recommendedAt) {
        this.recommendedAt = recommendedAt;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvent(List<Event> events) {
        this.events = events;
    }

    public Set<Suggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(Set<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }
    
    /**
     * @deprecated Use {@link Suggestion} to get information about the recommender.
     */
    @Nullable
    @Deprecated
    public User getRecommender() {
        return recommender;
    }

    /**
     * @deprecated Use {@link Suggestion} to store information about the recommender.
     */
    @Deprecated
    public void setRecommender(User recommender) {
        this.recommender = recommender;
    }
}
