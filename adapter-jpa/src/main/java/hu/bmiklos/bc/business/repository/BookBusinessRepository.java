package hu.bmiklos.bc.business.repository;

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
    return bookRepository.findByEventsIsEmpty().stream().map(bookMapper::convert).toList();
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
}
