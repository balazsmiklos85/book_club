package hu.bmiklos.bc.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class BookAgeDeterminator {

    private BookDto book;

    public BookAgeDeterminator(BookDto book) {
        this.book = book;
    }

    public boolean isFromTheLastMonth() {
        return book.getSuggesters()
            .stream()
            .map(SuggestionDto::getSuggestedAt)
            .filter(BookAgeDeterminator::isFromTheLastMonth)
            .findAny()
            .isPresent();
    }

    private static boolean isFromTheLastMonth(Instant instant) {
        return instant.isAfter(Instant.now().minus(30l, ChronoUnit.DAYS));
    }
}
