package hu.bmiklos.bc.service;

import static java.lang.Math.max;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Vote;

public class BookWeights {
    private final Map<Book, Long> weights;
    private final UserWeights userWeights;

    public BookWeights(Collection<Vote> votes, UserWeights userWeights) {
        this.userWeights = userWeights;
        weights = votes.stream()
                .collect(Collectors.groupingBy(Vote::getBook, Collectors.summingLong(this::getUserWeight)));
    }

    public Long getWeight(Book book) {
        return weights.getOrDefault(book, 0L);
    }

    private Long getUserWeight(Vote vote) {
        return max(userWeights.getWeight(vote.getUserById()), userWeights.getWeight(vote.getUserExternalId()));
    }
}
