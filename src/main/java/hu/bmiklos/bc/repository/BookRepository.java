package hu.bmiklos.bc.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.bmiklos.bc.model.Book;

public interface BookRepository extends JpaRepository<Book, UUID> {}