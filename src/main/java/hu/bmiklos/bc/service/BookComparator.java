package hu.bmiklos.bc.service;

import java.time.Instant;
import java.util.Comparator;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;

public class BookComparator implements Comparator<Book> {
    private final BookWeights bookWeights;

    public BookComparator(BookWeights bookWeights) {
        this.bookWeights = bookWeights;
    }

    @Override
    public int compare(Book book1, Book book2) {
        int result = bookWeights.getWeight(book2).compareTo(bookWeights.getWeight(book1));

        if (result == 0) {
            Instant recommendedAt1 = getRecommendedAt(book1);
            Instant recommendedAt2 = getRecommendedAt(book2);
            result = recommendedAt1.compareTo(recommendedAt2);
        }

        return result;        
    }

    private Instant getRecommendedAt(Book book) {
        return java.util.Optional.ofNullable(book.getRecommendedAt())
            .orElse(book.getSuggestions()
                .stream()
                .map(Suggestion::getCreationDate)
                .min(Instant::compareTo)
                .orElse(Instant.MIN));
    }
}
