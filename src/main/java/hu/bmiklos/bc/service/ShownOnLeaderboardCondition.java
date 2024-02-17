package hu.bmiklos.bc.service;

import java.util.Objects;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;

import hu.bmiklos.bc.model.Book;

public class ShownOnLeaderboardCondition implements Predicate<Book> {

    @Override
    public boolean test(Book book) {
        return (hasSuggestion(book) || hasRecommender(book))
            && hasNoEvent(book);
    }

    private boolean hasNoEvent(Book book) {
        return CollectionUtils.isEmpty(book.getEvents());
    }

    private boolean hasRecommender(Book book) {
        return Objects.nonNull(book.getRecommenderExternalId())
            || Objects.nonNull(book.getRecommender());
    }

    private boolean hasSuggestion(Book book) {
        return CollectionUtils.isNotEmpty(book.getSuggestions());
    }
}
