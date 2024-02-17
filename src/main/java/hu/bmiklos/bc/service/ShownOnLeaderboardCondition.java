package hu.bmiklos.bc.service;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;

import hu.bmiklos.bc.model.Book;

public class ShownOnLeaderboardCondition implements Predicate<Book> {

    @Override
    public boolean test(Book book) {
        return hasSuggestion(book) || (hasRecommender(book)
            && hasNoEvent(book));
    }

    private boolean hasNoEvent(Book book) {
        return CollectionUtils.isEmpty(book.getEvents());
    }

    private boolean hasRecommender(Book book) {
        return Stream.of(book.getRecommender(), book.getRecommenderExternalId())
                .anyMatch(Objects::nonNull);
    }

    private boolean hasSuggestion(Book book) {
        return CollectionUtils.isNotEmpty(book.getSuggestions());
    }
}
