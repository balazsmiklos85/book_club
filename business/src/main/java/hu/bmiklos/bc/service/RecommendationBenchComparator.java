package hu.bmiklos.bc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;

/**
 * A comparator for sorting books based on whether they are in the top 5
 * recommended books for any user. The books are sorted in descending order, so
 * the top 5 books are first.
 * 
 * This is done to implement what was the "recommendation bench" in the old
 * Excel sheet: each user could only have 5 books for voting, the rest of them
 * went to the recommendation bench.
 */
public class RecommendationBenchComparator implements Comparator<Book> {
    private final Map<User, List<UUID>> userTop5;
    private final Map<Integer, List<UUID>> historicUserTop5;
    private final List<UUID> allTop5Books;

    /**
     * This class represents a comparator for book recommendations. It compares
     * books based on whether they are in the top 5 recommended books for any user.
     *
     * @param books A list of books to be used for comparison. The sorting order of
     *              the list is used to determine the top 5 status.
     */
    public RecommendationBenchComparator(List<Book> books) {
        this.userTop5 = new HashMap<>();
        this.historicUserTop5 = new HashMap<>();
        for (Book book : books) {
            Collection<User> suggesters = book.getSuggestions()
                    .stream()
                    .map(Suggestion::getSuggester)
                    .toList();
            if (suggesters == null || suggesters.isEmpty()) {
                suggesters = Optional.ofNullable(book.getRecommender())
                        .map(List::of)
                        .orElse(List.of());
            }
            Integer historicRecommender = book.getRecommenderExternalId();
            if (suggesters.isEmpty()) {
                List<UUID> result;
                Optional<User> userByHistory = userTop5.keySet().stream()
                        .filter(user -> Objects.equals(user.getExternalId(), historicRecommender))
                        .findAny();
                if (userByHistory.isPresent()) {
                    result = userTop5.get(userByHistory.get());
                } else {
                    result = historicUserTop5.computeIfAbsent(historicRecommender, k -> new ArrayList<>());
                }
                List<UUID> top5 = result;
                if (top5.size() < 5) {
                    top5.add(book.getId());
                }
            } else {
                for (User recommender : suggesters) {
                    List<UUID> result;
                    if (recommender != null && recommender.getId() != null) {
                        result = userTop5.computeIfAbsent(recommender, k -> new ArrayList<>());
                    } else {
                        Optional<User> userByHistory = userTop5.keySet().stream()
                                .filter(user -> Objects.equals(user.getExternalId(), historicRecommender))
                                .findAny();
                        if (userByHistory.isPresent()) {
                            result = userTop5.get(userByHistory.get());
                        } else {
                            result = historicUserTop5.computeIfAbsent(historicRecommender, k -> new ArrayList<>());
                        }
                    }
                    List<UUID> top5 = result;
                    if (top5.size() < 5) {
                        top5.add(book.getId());
                    }
                }
            }
        }
        this.allTop5Books = new ArrayList<>();
        for (List<UUID> top5 : userTop5.values()) {
            allTop5Books.addAll(top5);
        }
        for (List<UUID> top5 : historicUserTop5.values()) {
            allTop5Books.addAll(top5);
        }
    }

    /**
     * Compares two books based on whether they are in the top 5 recommended books.
     * 
     * @param book1 the first book to compare
     * @param book2 the second book to compare
     * @return a negative integer, zero, or a positive integer as the first argument
     *         is less than, equal to, or greater than the second.
     */
    @Override
    public int compare(Book book1, Book book2) {
        Boolean book1IsTop5 = allTop5Books.contains(book1.getId());
        Boolean book2IsTop5 = allTop5Books.contains(book2.getId());
        return book2IsTop5.compareTo(book1IsTop5);
    }
}
