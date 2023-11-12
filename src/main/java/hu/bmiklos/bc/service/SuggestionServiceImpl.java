package hu.bmiklos.bc.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.repository.SuggestionRepository;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.mapper.SuggestionMapper;

@Service
public class SuggestionServiceImpl extends AuthenticatedService implements SuggestionService {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final SuggestionRepository suggestionRepository;

    public SuggestionServiceImpl(BookService bookService, BookRepository bookRepository, SuggestionRepository suggestionRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    public void createSuggestion(CreateBookRequest book) {
        var createdBook = bookService.createBook(book);
        var suggestion = new Suggestion(createdBook.getId(), getUserId(), Instant.now(), book.getDescription());
        suggestionRepository.saveAndFlush(suggestion);
    }

    @Override
    @Nullable
    public SuggestionDto getSuggestion(UUID id) {
        Optional<Suggestion> suggestion = suggestionRepository.findById(id);
        if (suggestion.isPresent()) {
            return SuggestionMapper.mapToDto(suggestion.get());
        }
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return SuggestionMapper.mapToDto(book.get());
        }
        return null;
    }
}