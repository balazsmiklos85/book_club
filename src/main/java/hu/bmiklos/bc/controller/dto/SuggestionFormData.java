package hu.bmiklos.bc.controller.dto;

public class SuggestionFormData {
    private final String bookId;
    private final String suggestionId;
    private final String author;
    private final String title;
    private final String url;
    private final String description;
    private boolean isSuggestedByMe = false;

    public SuggestionFormData(String bookId, String suggestionId, String author, String title, String url, String description) {
        this.bookId = bookId;
        this.suggestionId = suggestionId;
        this.author = author;
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getBookId() {
        return bookId;
    }

    public String getSuggestionId() {
        return suggestionId;
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

    public String getDescription() {
        return description;
    }

    public boolean isSuggestedByMe() {
        return isSuggestedByMe;
    }

    public void setSuggestedByMe() {
        isSuggestedByMe = true;
    }    
}
