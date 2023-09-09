package hu.bmiklos.bc.service;

import java.util.Comparator;

import hu.bmiklos.bc.model.Book;

public class BookComparator implements Comparator<Book> {
    private final BookWeights bookWeights;

    public BookComparator(BookWeights bookWeights) {
        this.bookWeights = bookWeights;
    }

    @Override
    public int compare(Book book1, Book book2) {
        int result = bookWeights.getWeight(book2).compareTo(bookWeights.getWeight(book1));

        if (result == 0) {
            result = book1.getRecommendedAt().compareTo(book2.getRecommendedAt());
        }

        return result;        
    }

}
