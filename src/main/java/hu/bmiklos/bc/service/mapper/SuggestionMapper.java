package hu.bmiklos.bc.service.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;

import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class SuggestionMapper {
    private SuggestionMapper() {}

    public static List<SuggestionDto> mapToDto(Collection<Suggestion> suggestions) {
        return suggestions.stream()
            .map(SuggestionMapper::mapToDto)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * @deprecated Use {@link #mapToDto(Suggestion)} instead.
     */
    @Deprecated
    @Nullable
    public static SuggestionDto mapToDto(Book book) {
        User recommender = book.getRecommender();
        if (recommender == null) {
            return null;
        }
        return new SuggestionDto(null, book.getRecommendedAt(), UserMapper.mapToDto(recommender, book.getRecommenderExternalId()));
    }

    @Nullable
    public static SuggestionDto mapToDto(Suggestion suggestion) {
        User suggester = suggestion.getSuggester();
        if (suggester == null) {
            return null;
        }
        return new SuggestionDto(suggestion.getId(), suggestion.getCreationDate(), UserMapper.mapToDto(suggester));
    }

    public static SuggestionReference mapToReference(SuggestionDto suggestion) {
        return new SuggestionReference(suggestion.getId(), suggestion.getSuggester().getName());
    }
}