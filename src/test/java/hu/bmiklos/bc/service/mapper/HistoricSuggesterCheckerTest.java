package hu.bmiklos.bc.service.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;

class HistoricSuggesterCheckerTest {
    private HistoricSuggesterChecker checker;

    @BeforeEach
    void setup() {
        this.checker = new HistoricSuggesterChecker();
    }

    @Test
    void findsHistoricSuggester() {
        var book = new Book("Test Author", "Test Title", "test://url.hu");
        var suggester = new User();
        book.setRecommender(suggester);

        boolean result = checker.test(book);

        assertTrue(result, "The registered historic suggester should be found.");
    }

    @Test
    void findsNonregisteredHistoricSuggester() {
        var book = new Book("Test Author", "Test Title", "test://url.hu");
        book.setRecommenderExternalId(-1);

        boolean result = checker.test(book);

        assertTrue(result, "The historic suggester should be found.");
    }

    @Test
    void detectsNoHistoricRecords() {
        var book = new Book("Test Author", "Test Title", "test://url.hu");

        boolean result = checker.test(book);

        assertFalse(result, "If nothing is set, then there is nothing to find.");
    }

    @Test
    void detectsAlreadyReferencedUser() {
        var book = new Book("Test Author", "Test Title", "test://url.hu");
        var suggester = new User();
        suggester.setId(UUID.randomUUID());
        book.setRecommender(suggester);
        var suggestion = new Suggestion();
        suggestion.setUserId(suggester.getId());
        book.setSuggestions(Set.of(suggestion));

        boolean result = checker.test(book);

        assertFalse(result, "A historic suggestion that has already been converted to the new format should be ignored.");
    }
}
