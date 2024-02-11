package hu.bmiklos.bc.service.mapper;

import java.util.Optional;
import java.util.UUID;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.controller.dto.SuggestionFormData;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class SuggestionDtoToSuggestionFormDataConverter implements Converter<BookAndSuggesterDto, SuggestionFormData> {

    private final UUID userId;

    public SuggestionDtoToSuggestionFormDataConverter(UUID userId) {
        this.userId = userId;
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
                    .map(SuggestionDto::getSuggester)
                    .map(UserDto::getId)
                    .orElse(null);
                return userId.equals(suggesterUserId);
            })
            .findFirst();
    }
}
