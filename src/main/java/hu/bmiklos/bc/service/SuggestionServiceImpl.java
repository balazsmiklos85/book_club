package hu.bmiklos.bc.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.repository.SuggestionRepository;

@Service
public class SuggestionServiceImpl extends AuthenticatedService implements SuggestionService {

    private final BookService bookService;
    private final SuggestionRepository suggestionRepository;

    public SuggestionServiceImpl(BookService bookService, SuggestionRepository suggestionRepository) {
        this.bookService = bookService;
        this.suggestionRepository = suggestionRepository;
    }

    @Override
    public void createSuggestion(CreateBookRequest book) {
        var createdBook = bookService.createBook(book);
        var suggestion = new Suggestion(createdBook.getId(), getUserId(), Instant.now(), book.getDescription());
        suggestionRepository.saveAndFlush(suggestion);
    }
}