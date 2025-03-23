package hu.bmiklos.bc.business.repository;

import hu.bmiklos.bc.domain.entities.Book;
import java.util.List;

public interface BookRepository {
  List<Book> findAll();
}
