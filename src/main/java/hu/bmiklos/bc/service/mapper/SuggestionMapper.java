package hu.bmiklos.bc.service.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.Nullable;

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

    @Nullable
    private static SuggestionDto mapToDto(Suggestion suggestion) {
        User suggester = suggestion.getSuggester();
        if (suggester == null) {
            return null;
        }
        return new SuggestionDto(suggestion.getCreationDate(), UserMapper.mapToDto(suggester));
    }
}