package hu.bmiklos.bc.domain.functions;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Suggestion;
import java.util.*;

public class RecommendationBenchComparator implements Comparator<Book> {
  private final Set<UUID> topBooks;

  public RecommendationBenchComparator(List<Book> presortedBooks) {
    this.topBooks = new HashSet<>();
    Map<Integer, Integer> suggesterBookCount = new HashMap<>();
    for (Book book : presortedBooks) {
      for (Suggestion suggestion : book.getSuggestions()) {
        Integer suggester = suggestion.getSuggester().externalId();
        int rankBySuggester = suggesterBookCount.computeIfAbsent(suggester, (ignored) -> 0) + 1;
        if (rankBySuggester <= 5) {
          this.topBooks.add(book.getId());
        }
        suggesterBookCount.put(suggester, rankBySuggester);
      }
    }
  }

  @Override
  public int compare(Book book1, Book book2) {
    return Boolean.compare(topBooks.contains(book2.getId()), topBooks.contains(book1.getId()));
  }
}
