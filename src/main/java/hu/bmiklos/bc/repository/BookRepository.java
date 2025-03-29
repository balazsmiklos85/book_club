package hu.bmiklos.bc.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.business.repository.BookBusinessRepository;
import hu.bmiklos.bc.model.Book;

/**
 * @deprecated Use {@link BookBusinessRepository} in the business layer, and {@link BookJpaRepository} in the JPA adapter.
 */
@Deprecated
public interface BookRepository extends JpaRepository<Book, UUID> {

    @EntityGraph(value = "Book.recommenderInfo", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findAll();

    Optional<Book> findByUrl(String url);
}
