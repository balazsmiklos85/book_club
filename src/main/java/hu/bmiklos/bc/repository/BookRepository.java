package hu.bmiklos.bc.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {

    @EntityGraph(value = "Book.recommenderInfo", type = EntityGraph.EntityGraphType.LOAD)
    List<Book> findAll();

    Optional<Book> findByUrl(String url);
}
