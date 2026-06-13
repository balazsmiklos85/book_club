package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Book;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BookRepository {
  List<Book> findAllWithoutEvents();

  Map<UUID, Integer> getBookWeights();

  Book findById(UUID fromString);

  void clearLegacyRecommender(UUID bookId);
}
