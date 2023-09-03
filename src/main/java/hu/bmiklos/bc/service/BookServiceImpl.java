package hu.bmiklos.bc.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;
import jakarta.transaction.Transactional;

@Service
public class BookServiceImpl extends AuthenticatedService implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public Book createBook(CreateBookRequest book) {
        Book toSave = new Book(book.getAuthor(), book.getTitle(), book.getUrl(), getExternalUserId(), Instant.now());
        return bookRepository.saveAndFlush(toSave);
    }
}
