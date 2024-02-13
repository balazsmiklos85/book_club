package hu.bmiklos.bc.service.dto;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class BookAndSuggesterDto {
    private final BookDto book;
    private final Set<SuggestionDto> suggesters;

    public BookAndSuggesterDto(UUID id, String author, String title, String url, Collection<SuggestionDto> suggesters) {
        this.book = new BookDto(id, author, title, url);
        this.suggesters = Set.copyOf(suggesters);
    }

    public BookDto getBook() {
        return book;
    }

    public Set<SuggestionDto> getSuggesters() {
        return suggesters;
    }
}
