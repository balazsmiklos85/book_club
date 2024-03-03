package hu.bmiklos.bc.controller.mapper;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;

import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.mapper.SuggestionMapper;

public class SuggestionsConverter implements
    Converter<Collection<SuggestionDto>, Collection<SuggestionReference>> {

    @Override
    public Collection<SuggestionReference> convert(Collection<SuggestionDto> source) {
        return source.stream()
            .map(SuggestionMapper::mapToReference)
            .toList();
    }
}
