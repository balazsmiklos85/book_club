package hu.bmiklos.bc.service.dto;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BookDto {
    private final UUID id;
    private final String author;
    private final String title;
    private final String url;
    private final Integer historicSuggester;
    private final Set<SuggestionDto> suggesters;

    public BookDto(UUID id, String author, String title, String url, Collection<SuggestionDto> suggesters) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
        this.historicSuggester = null;
        this.suggesters = Set.copyOf(suggesters);
    }

    public BookDto(UUID id, String author, String title, String url, Integer historicSuggester) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
        this.historicSuggester = historicSuggester;
        this.suggesters = Set.of();
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

    public Optional<Integer> getHistoricSuggester() {
        return Optional.ofNullable(historicSuggester);
    }

    public Set<SuggestionDto> getSuggesters() {
        return suggesters;
    }
}
