package hu.bmiklos.bc.business.usecase;

import hu.bmiklos.bc.business.repository.BookRepository;
import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.functions.RecommendationBenchComparator;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SortedBookQueryService {
  private final BookRepository bookRepository;

  public List<Book> getLeaderboardBooks() {
    Map<UUID, Integer> bookWeights = bookRepository.getBookWeights();
    List<Book> allBooks =
        bookRepository.findAllWithoutEvents().stream()
            .sorted(Comparator.comparing(book -> bookWeights.getOrDefault(book.getId(), 0)))
            .toList();
    var benchComparator = new RecommendationBenchComparator(allBooks);
    return allBooks.stream().sorted(benchComparator).toList();
  }
}
