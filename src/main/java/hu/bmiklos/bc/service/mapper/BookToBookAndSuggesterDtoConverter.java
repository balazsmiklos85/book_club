package hu.bmiklos.bc.service.mapper;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class BookToBookAndSuggesterDtoConverter implements Converter<Book, BookAndSuggesterDto> {

    @Override
    @NonNull
    public BookAndSuggesterDto convert(@NonNull Book book) {
        final Set<SuggestionDto> suggesters = new HashSet<>(1);
        if (new HistoricSuggesterChecker().test(book)) {
            var suggester = new SuggestionDto(book.getRecommendedAt(),
                    UserMapper.mapToDto(book.getRecommender(),
                    book.getRecommenderExternalId()), "");
            suggesters.add(suggester);
        }
        if (CollectionUtils.isNotEmpty(book.getSuggestions())) {
            suggesters.addAll(SuggestionMapper.mapToDto(book.getSuggestions()));
        }
        BookDto convertedBook = new BookToBookDtoConverter().convert(book);
        return new BookAndSuggesterDto(convertedBook, suggesters);
    }
}

