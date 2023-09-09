package hu.bmiklos.bc.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Vote;

public class BookWeights {
    private final Map<Book, Long> weights;

    public BookWeights(List<Vote> votes, UserWeights userWeights) {
        weights = votes.stream()
                .collect(Collectors.groupingBy(
                        Vote::getBook,
                        Collectors.summingLong(vote -> userWeights.getWeight(vote.getUserById()) + userWeights.getWeight(vote.getUserExternalId()))));

    }

    public Long getWeight(Book book) {
        return weights.getOrDefault(book, 0L);
    }
}
