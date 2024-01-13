package hu.bmiklos.bc.service;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.controller.dto.CreateBookRequest;
import hu.bmiklos.bc.controller.dto.SuggestionFormData;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.repository.SuggestionRepository;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.mapper.BookMapper;
import hu.bmiklos.bc.service.mapper.SuggestionMapper;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SuggestionServiceImpl extends AuthenticatedService implements SuggestionService {

    private final ActiveUserService activeUserService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final SuggestionRepository suggestionRepository;

    public SuggestionServiceImpl(ActiveUserService activeUserService, BookService bookService,
            BookRepository bookRepository, SuggestionRepository suggestionRepository) {
        this.activeUserService = activeUserService;
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
        return book.map(SuggestionMapper::mapToDto)
                .map(BookAndSuggesterDto::getSuggesters)
                .map(Collection::stream)
                .map(Stream::findFirst)
                .map(Optional::get)
                .orElse(null);
    }

    @Override
    public BookAndSuggesterDto getBookBySuggestionId(UUID id) {
        Optional<Suggestion> suggestion = suggestionRepository.findById(id);

        if (suggestion.isPresent()) {
            return suggestion.map(Suggestion::getBook)
                    .map(BookMapper::mapToDto)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found."));
        }

        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return BookMapper.mapToDto(book.get());
        }
        throw new EntityNotFoundException("Suggestion or book not found.");
    }

    @Override
    public void updateSuggestion(UUID id, SuggestionFormData suggestionData) {
        Optional<Suggestion> storedSuggestion = suggestionRepository.findById(id);
        if (storedSuggestion.isPresent()) {
            Suggestion suggestion = storedSuggestion.get();
            suggestion.setDescription(suggestionData.getDescription());
            suggestionRepository.saveAndFlush(storedSuggestion.get());
        } else {
            UUID bookId = UUID.fromString(suggestionData.getBookId());
            Optional<Book> storedBook = bookRepository.findById(bookId);
            if (storedBook.isPresent()) {
                Book book = storedBook.get();
                User bookRecommender = book.getRecommender();
                if (activeUserService.isCurrentUser(bookRecommender.getId())
                        || activeUserService.isCurrentUser(bookRecommender.getExternalId())) {
                    var suggestion = new Suggestion(bookId, getUserId(), book.getRecommendedAt(),
                            suggestionData.getDescription());
                    suggestionRepository.saveAndFlush(suggestion);
                    book.setRecommender(null);
                    book.setRecommendedAt(null);
                    book.setRecommenderExternalId(null);
                    bookRepository.save(book);
                } else {
                    throw new IllegalArgumentException("You can only edit your own suggestions.");
                }
            } else {
                throw new EntityNotFoundException("Previous suggestion not found.");
            }
        }
    }
}
