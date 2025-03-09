package hu.bmiklos.bc.service.mapper;

import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;

public class HistoricSuggesterChecker implements Predicate<Book> {

    /**
     * Checks if a {@link Book} still stores a valid and unique book suggestion.
     * A book suggestion is considered valid and unique, if:
     * <ol>
     *  <li>Either the recommender external ID is set for the {@link Book}.</li>
     *  <li>Or the recommender {@link User} is set for the {@link Book}.</li>
     *  <li>And none of the above fields point to the same {@link User} as any other
     *  {@link Suggestion} that belongs to the {@link Book}
     * </ol>
     * @param book The {@link Book} that may or may not still contain a
     * suggestion.
     * @return true if a historic suggestion is found, false otherwise.
     */
    @Override
    public boolean test(Book book) {
        if (book.getRecommender() == null
                && book.getRecommenderExternalId() == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(book.getSuggestions())) {
            return true;
        }
        final var idChecker = new HistoricIdChecker(book);
        return book.getSuggestions()
            .stream()
            .noneMatch(idChecker::test);
    }
}
