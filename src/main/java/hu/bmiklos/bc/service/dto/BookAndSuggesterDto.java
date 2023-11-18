package hu.bmiklos.bc.service.dto;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BookAndSuggesterDto {
    private final BookDto book;
    private final Integer historicSuggester;
    private final Set<SuggestionDto> suggesters;

    public BookAndSuggesterDto(UUID id, String author, String title, String url, Collection<SuggestionDto> suggesters) {
        this.book = new BookDto(id, author, title, url);
        this.historicSuggester = null;
        this.suggesters = Set.copyOf(suggesters);
    }

    public BookAndSuggesterDto(UUID id, String author, String title, String url, Integer historicSuggester) {
        this.book = new BookDto(id, author, title, url);
        this.historicSuggester = historicSuggester;
        this.suggesters = Set.of();
    }

    public BookDto getBook() {
        return book;
    }

    /**
     * @deprecated Use {@link #getSuggesters()} instead whenever possible.
     */
    @Deprecated
    public Optional<Integer> getHistoricSuggester() {
        return Optional.ofNullable(historicSuggester);
    }

    public Set<SuggestionDto> getSuggesters() {
        return suggesters;
    }
}