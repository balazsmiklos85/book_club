package hu.bmiklos.bc.service;

import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public class BookAgeDeterminator {

  private Set<SuggestionDto> suggesters;

  public BookAgeDeterminator(BookAndSuggesterDto book) {
    this.suggesters = Set.copyOf(book.suggestions());
  }

  public boolean isFromTheLastMonth() {
    return suggesters.stream()
        .map(SuggestionDto::getSuggestedAt)
        .anyMatch(BookAgeDeterminator::isFromTheLastMonth);
  }

  private static boolean isFromTheLastMonth(Instant instant) {
    return instant.isAfter(Instant.now().minus(30l, ChronoUnit.DAYS));
  }
}
