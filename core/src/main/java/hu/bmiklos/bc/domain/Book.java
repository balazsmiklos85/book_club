package hu.bmiklos.bc.domain;

import java.util.UUID;

public class Book {
  private final UUID id;
  private final String author;
  private final String title;
  private final String url;

  public Book(final UUID id, final String author, final String title, final String url) {
    this.author = author;
    this.title = title;
    this.url = url;
    this.id = id;
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

