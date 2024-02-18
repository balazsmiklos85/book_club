package hu.bmiklos.bc.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Suggestion;
import hu.bmiklos.bc.model.User;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class SuggestionRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SuggestionRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void deletesSuggestionsByBookId() {
        var book = new Book("Test Author", "Test Title", "test://url.hu");
        Book persistedBook = bookRepository.save(book);
        var bookId = persistedBook.getId();
        var user = new User("Test User", false, -1);
        User persistedUser = userRepository.save(user);
        var userId = persistedUser.getId();
        Suggestion suggestion = new Suggestion();
        suggestion.setId(UUID.randomUUID());
        suggestion.setBookId(bookId);
        suggestion.setUserId(userId);
        suggestion.setCreationDate(Instant.now());
        suggestion.setDescription("Test description");
        repository.save(suggestion);
        List<Suggestion> persistedSuggestions = repository.findAll();
        assertEquals(1, persistedSuggestions.size());

        repository.deleteByBookId(bookId);

        persistedSuggestions = repository.findAll();
        assertEquals(0, persistedSuggestions.size());

        userRepository.delete(persistedUser);
        bookRepository.delete(persistedBook);
    }
}

