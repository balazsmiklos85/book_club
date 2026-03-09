package hu.bmiklos.bc.business.repository;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import hu.bmiklos.bc.business.mapper.BookEntityMapper;
import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.model.entity.BookWeight;
import hu.bmiklos.bc.model.repository.BookJpaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookBusinessRepository implements BookRepository {

  private final BookJpaRepository bookRepository;

  @Override
  public List<Book> findAllWithoutEvents() {
    var bookMapper = new BookEntityMapper();
    return bookRepository.findByEventsIsEmpty().stream()
        .filter(
            b ->
                isNotEmpty(b.getSuggestions())
                    || nonNull(b.getRecommender())
                    || nonNull(b.getRecommenderExternalId()))
        .map(bookMapper::convert)
        .toList();
  }

  @Override
  public Map<UUID, Integer> getBookWeights() {
    return bookRepository.findBookWeights().stream()
        .map(row -> new BookWeight((UUID) row[0], (BigDecimal) row[1]))
        .collect(
            Collectors.groupingBy(
                BookWeight::bookId,
                Collectors.summingInt(weight -> -1 * weight.weight().intValue())));
  }

  @Override
  public Book findById(UUID id) {
    var bookMapper = new BookEntityMapper();
    return bookRepository.findById(id)
      .stream()
      .filter(b -> isNotEmpty(b.getSuggestions())
          || nonNull(b.getRecommender())
          || nonNull(b.getRecommenderExternalId()))
      .map(bookMapper::convert)
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Could not find book " + id + ". No suggester maybe?"));
  }
}
