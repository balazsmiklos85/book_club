package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.business.mapper.BookEntityMapper;
import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.model.repository.BookJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookBusinessRepository implements BookRepository {

  private final BookJpaRepository bookRepository;

  @Override
  public List<Book> findAll() {
    var mapper = new BookEntityMapper();
    return bookRepository.findAll().stream().map(mapper::convert).toList();
  }
}
