package hu.bmiklos.bc.service.mapper;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.lang.NonNull;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;

/**
 * Checks if a {@link Suggestion} is already present as a historic "suggestion", when the suggester was just stored in
 * the {@link Book} in the recommender field for registered users, and in the external ID field for users who were
 * still coming from the old Excel sheet.
 */
public class HistoricIdChecker implements Predicate<Suggestion> {

    private final UUID recommenderId;
    private final Integer recommenderExternalId;

    public HistoricIdChecker(@NonNull Book book) {
        this.recommenderId = Optional.ofNullable(book.getRecommender())
            .map(User::getId)
            .orElse(null);
        this.recommenderExternalId = book.getRecommenderExternalId();
    }

    @Override
    public boolean test(Suggestion suggestion) {
        UUID id = suggestion.getUserId();
        Integer externalId = Optional.ofNullable(suggestion.getSuggester())
            .map(User::getExternalId)
            .orElse(null);
        boolean hasSameId = Objects.nonNull(id) && Objects.equals(id, recommenderId);
        boolean hasSameExternalId = Objects.nonNull(externalId) && Objects.equals(externalId, recommenderExternalId);
        return hasSameId || hasSameExternalId;
    }
}
