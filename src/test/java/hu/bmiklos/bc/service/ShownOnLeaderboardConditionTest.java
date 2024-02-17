package hu.bmiklos.bc.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;

class ShownOnLeaderboardConditionTest {
    private ShownOnLeaderboardCondition condition;

    @BeforeEach
    void setup() {
        this.condition = new ShownOnLeaderboardCondition();
    }

    @Test
    void suggestedShowsOnLeaderboard() {
        var book = new Book();
        var suggestion = new Suggestion();
        book.setSuggestions(Set.of(suggestion));

        boolean result = condition.test(book);

        assertTrue(result, "The suggested book without event should be shown on the leaderboard.");
    }

    @Test
    void recommendedWithoutRegistrationShowsOnLeaderboard() {
        var book = new Book();
        book.setRecommenderExternalId(-1);

        boolean result = condition.test(book);

        assertTrue(result, "The recommended book without event should be shown on the leaderboard.");
    }

    @Test
    void recommendedShowsOnLeaderboard() {
        var book = new Book();
        var suggester = new User();
        book.setRecommender(suggester);

        boolean result = condition.test(book);

        assertTrue(result, "The recommended book without event should be shown on the leaderboard.");
    }

    @Test
    void recommendedWithEventDoesNotShowOnLeaderboard() {
        var book = new Book();
        book.setRecommenderExternalId(-1);
        book.setEvent(List.of(new Event()));

        boolean result = condition.test(book);

        assertFalse(result, "The suggested book with an event should not be shown on the leaderboard.");
    }


}
