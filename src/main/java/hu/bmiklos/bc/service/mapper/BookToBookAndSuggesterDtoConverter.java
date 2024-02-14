package hu.bmiklos.bc.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class BookToBookAndSuggesterDtoConverter implements Converter<Book, BookAndSuggesterDto> {

    @Override
    @NonNull
    public BookAndSuggesterDto convert(@NonNull Book book) {
        final List<SuggestionDto> suggesters = new ArrayList<>(1);
        if (hasHistoricRecommender(book)) {
            var suggester = new SuggestionDto(book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(),
                    book.getRecommenderExternalId()), "");
            suggesters.add(suggester);
        }
        if (CollectionUtils.isNotEmpty(book.getSuggestions())) {
            suggesters.addAll(SuggestionMapper.mapToDto(book.getSuggestions()));
        }
        return new BookAndSuggesterDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters);
    }

    private boolean hasHistoricRecommender(Book book) {
        boolean result = book.getRecommender() != null || book.getRecommenderExternalId() != null;
        if (!result) {
            return false;
        }
        if (CollectionUtils.isEmpty(book.getSuggestions())) {
            return result;
        }
        UUID recommenderId = Optional.ofNullable(book.getRecommender())
            .map(User::getId)
            .orElse(null);
        Integer recommenderExternalId = book.getRecommenderExternalId();
        return book.getSuggestions().stream()
            .map(Suggestion::getSuggester)
            .noneMatch(suggester -> Objects.equals(suggester.getId(), recommenderId)
                    || Objects.equals(suggester.getExternalId(), recommenderExternalId));
    }
}

