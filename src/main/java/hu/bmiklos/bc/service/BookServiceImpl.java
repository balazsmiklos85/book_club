package hu.bmiklos.bc.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.mapper.BookMapper;
import jakarta.persistence.EntityNotFoundException;
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
        Optional<Book> storedBook = bookRepository.findByUrl(book.getUrl());
        if (storedBook.isPresent()) {
            return storedBook.get();
        }

        Book toSave = new Book(book.getAuthor(), book.getTitle(), book.getUrl());
        return bookRepository.saveAndFlush(toSave);
    }

    @Override
    public BookDto getBookById(String rawId) {
        var bookId = UUID.fromString(rawId);
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new EntityNotFoundException("Book not found."));
        return BookMapper.mapToDto(book);
    }
}
