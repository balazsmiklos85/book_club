package hu.bmiklos.bc.service.dto;

import java.util.UUID;

public class BookDto {
    private final UUID id;
    private final String author;
    private final String title;
    private final String url;

    public BookDto(UUID id, String author, String title, String url) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
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
}
