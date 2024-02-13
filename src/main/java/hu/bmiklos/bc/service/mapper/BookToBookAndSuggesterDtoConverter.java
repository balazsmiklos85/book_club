package hu.bmiklos.bc.service.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class BookToBookAndSuggesterDtoConverter implements Converter<Book, BookAndSuggesterDto> {

    @Override
    @NonNull
    public BookAndSuggesterDto convert(@NonNull Book book) {
        final List<SuggestionDto> suggesters = new ArrayList<>(1);
        if (hasOnlyUnregisteredHistoricSuggester(book)) {
            var suggester = new SuggestionDto(book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(),
                    book.getRecommenderExternalId()), "");
            suggesters.add(suggester);
            return new BookAndSuggesterDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters);
        }
        if (hasHistoricSuggester(book)) {
            var suggester = new SuggestionDto(book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(),
                    book.getRecommenderExternalId()), "");
            suggesters.add(suggester);
        }
        if (hasSuggesters(book)) {
            suggesters.addAll(SuggestionMapper.mapToDto(book.getSuggestions()));
        }
        return new BookAndSuggesterDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters);
    }

    private boolean hasHistoricSuggester(Book book) {
        return book.getRecommender() != null || book.getRecommenderExternalId() != null;
    }

    private boolean hasOnlyUnregisteredHistoricSuggester(Book book) {
        return book.getSuggestions() == null || book.getSuggestions().isEmpty()
            && book.getRecommender() == null;
    }

    private boolean hasSuggesters(Book book) {
        return book.getSuggestions() != null && !book.getSuggestions().isEmpty();
    }
}

