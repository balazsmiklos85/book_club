package hu.bmiklos.bc.controller.dto;

public class BookData {
    private final String id;
    private final String title;
    private final String author;

    public BookData(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}