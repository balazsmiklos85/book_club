package hu.bmiklos.bc.service.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;

import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
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
     * @deprecated Should be inlined at some point.
     */ 
    @Deprecated
    @Nullable
    public static BookAndSuggesterDto mapToDto(Book book) {
        return new BookToBookAndSuggesterDtoConverter().convert(book);
    }

    @Nullable
    public static SuggestionDto mapToDto(Suggestion suggestion) {
        User suggester = suggestion.getSuggester();
        if (suggester == null) {
            return null;
        }
        return new SuggestionDto(suggestion.getId(), suggestion.getCreationDate(), UserMapper.mapToDto(suggester), suggestion.getDescription());
    }

    public static SuggestionReference mapToReference(SuggestionDto suggestion) {
        String suggesterName;
        if (suggestion.getSuggester().getName() != null) {
            suggesterName = suggestion.getSuggester().getName();
        } else if (suggestion.getSuggester().getExternalId() != null) {
            suggesterName = "[" + suggestion.getSuggester().getExternalId() + "]";
        } else {
            suggesterName = "[N/A]";
        }
        return new SuggestionReference(suggestion.getId(), suggesterName);
    }
}
