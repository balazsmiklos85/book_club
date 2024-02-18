package hu.bmiklos.bc.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.repository.SuggestionRepository;

@ExtendWith(MockitoExtension.class)
public class SuggestionServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private SuggestionServiceImpl suggestionService;

    @Test
    void removesSuggesionsForBook() {
        var bookId = UUID.randomUUID();

        suggestionService.removeForBook(bookId);

        verify(suggestionRepository, times(1)).deleteByBookId(bookId);
    }

    @Test
    void removesRecommendationForBook() {
        var bookId = UUID.randomUUID();
        var book = new Book();
        book.setId(bookId);
        book.setRecommenderExternalId(-1);
        book.setRecommendedAt(Instant.now());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        suggestionService.removeForBook(bookId);

        assertNull(book.getRecommenderExternalId(),
                "The historic suggester should have been removed from the book.");
        assertNull(book.getRecommendedAt(),
                "The time of the historic suggestion should have been removed from the book.");
    }
}

