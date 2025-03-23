package hu.bmiklos.bc.business.usecase;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.business.repository.BookRepository;
import hu.bmiklos.bc.domain.entities.Book;

@Service
@RequiredArgsConstructor
public class SortedBookQueryService {
  private final BookRepository bookRepository;

  public List<Book> getAll() {
    return bookRepository.findAll();
  }
}
