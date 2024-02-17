package hu.bmiklos.bc.service.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;

class HistoricIdCheckerTest {
    @Test
    void hasSameId() {
        var book = new Book();
        var suggester = new User();
        suggester.setId(UUID.randomUUID());
        book.setRecommender(suggester);
        var checker = new HistoricIdChecker(book);
        var suggestion = new Suggestion();
        suggestion.setUserId(suggester.getId());

        boolean result = checker.test(suggestion);

        assertTrue(result, "Suggestion should be found by the same user ID.");
    }

    @Test
    void hasSameExternalId() {
        var book = new Book();
        var suggester = new User();
        suggester.setExternalId(-1);
        book.setRecommenderExternalId(-1);
        var checker = new HistoricIdChecker(book);
        var suggestion = mock(Suggestion.class);
        when(suggestion.getSuggester()).thenReturn(suggester);

        boolean result = checker.test(suggestion);

        assertTrue(result, "Suggestion should be found by the same external ID.");
    }

    @Test
    void hasBothIdsTheSame() {
        var book = new Book();
        var suggester = new User();
        suggester.setId(UUID.randomUUID());
        suggester.setExternalId(-1);
        book.setRecommenderExternalId(-1);
        var checker = new HistoricIdChecker(book);
        var suggestion = mock(Suggestion.class);
        when(suggestion.getSuggester()).thenReturn(suggester);
        when(suggestion.getUserId()).thenReturn(suggester.getId());

        boolean result = checker.test(suggestion);

        assertTrue(result, "Suggestion should be found by the same IDs.");
    }

    @Test
    void hasBothIdsDifferent() {
        var book = new Book();
        var suggester = new User();
        suggester.setId(UUID.randomUUID());
        suggester.setExternalId(-1);
        book.setRecommenderExternalId(-2);
        var checker = new HistoricIdChecker(book);
        var suggestion = mock(Suggestion.class);
        when(suggestion.getSuggester()).thenReturn(suggester);
        when(suggestion.getUserId()).thenReturn(UUID.randomUUID());

        boolean result = checker.test(suggestion);

        assertFalse(result, "Suggestion should not be found by different IDs.");
    }
}

