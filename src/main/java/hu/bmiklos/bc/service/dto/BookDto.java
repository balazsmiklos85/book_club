package hu.bmiklos.bc.service.dto;

import java.time.Instant;
import java.util.UUID;

public class BookDto {
    private final UUID id;
    private final String author;
    private final String title;
    private final String url;
    private final Instant recommendedAt;
    private final UserDto recommender;

    public BookDto(UUID id, String author, String title, String url, Instant recommendedAt, UserDto recommender) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
        this.recommendedAt = recommendedAt;
        this.recommender = recommender;
    }

    public UUID getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Instant getRecommendedAt() {
        return recommendedAt;
    }

    public UserDto getRecommender() {
        return recommender;
    }    
}
