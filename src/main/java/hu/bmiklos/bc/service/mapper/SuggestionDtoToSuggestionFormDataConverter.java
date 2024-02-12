package hu.bmiklos.bc.service.mapper;

import java.util.Optional;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.controller.dto.SuggestionFormData;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class SuggestionDtoToSuggestionFormDataConverter implements Converter<BookAndSuggesterDto, SuggestionFormData> {

    private final UUID suggestionId;

    public SuggestionDtoToSuggestionFormDataConverter(@NonNull UUID suggestionId) {
        this.suggestionId = suggestionId;
    }

    @Override
    @NonNull
    public SuggestionFormData convert(BookAndSuggesterDto source) {
        String bookId = source.getBook().getId().toString();
        String suggestionId = getSuggester(source)
            .map(SuggestionDto::getId)
            .map(Object::toString)
            .orElse(bookId);
        String author = source.getBook().getAuthor();
        String title = source.getBook().getTitle();
        String url = source.getBook().getUrl();
        String description = getSuggester(source)
            .map(SuggestionDto::getDescription)
            .orElse("");
        return new SuggestionFormData(bookId, suggestionId, author, title, url, description);
    }

    private Optional<SuggestionDto> getSuggester(BookAndSuggesterDto source) {
        return source.getSuggesters()
            .stream()
            .filter(suggestion -> {
                UUID suggesterUserId = Optional.ofNullable(suggestion)
                    .map(SuggestionDto::getId)
                    .orElse(null);
                return suggestionId.equals(suggesterUserId);
            })
            .findFirst();
    }
}
