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
@Table(name = "books")
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

    @Column(nullable = false)
    private int recommenderExternalId;

    @Column(nullable = false)
    private Instant recommendedAt;

    public Book() {}

    public Book(String author, String title, String url, int recommenderExternalId, Instant recommendedAt) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommenderExternalId = recommenderExternalId;
        this.recommendedAt = recommendedAt;
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

    public int getRecommenderExternalId() {
        return recommenderExternalId;
    }

    public void setRecommenderExternalId(int recommenderExternalId) {
        this.recommenderExternalId = recommenderExternalId;
    }

    public Instant getRecommendedAt() {
        return recommendedAt;
    }

    public void setRecommendedAt(Instant recommendedAt) {
        this.recommendedAt = recommendedAt;
    }

    
}