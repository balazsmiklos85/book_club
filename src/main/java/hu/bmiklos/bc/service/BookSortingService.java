package hu.bmiklos.bc.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.repository.EventRepository;
import hu.bmiklos.bc.repository.VoteRepository;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.mapper.BookToBookAndSuggesterDtoConverter;

@Service
public class BookSortingService {

    private final BookRepository bookRepository;

    private final EventRepository eventRepostiory;

    private final VoteRepository voteRepository;

    public BookSortingService(BookRepository bookRepository, EventRepository eventRepostiory,
            VoteRepository voteRepository) {
        this.bookRepository = bookRepository;
        this.eventRepostiory = eventRepostiory;
        this.voteRepository = voteRepository;
    }

    
    /**
     * Retrieves all books sorted by their recommendation score.
     * 
     * The recommendation score is calculated based on the votes, and the previous events which the voters attended. Books that already have a previous event declared are excluded from the list. Books that are not in the top 5 recommended books for their recommender, are punished, and pushed to the end of the list.
     * @return a list of {@link BookAndSuggesterDto} objects sorted by their recommendation score.
     */
    public List<BookAndSuggesterDto> getAll() {
        Collection<Vote> votes = voteRepository.findAll();
        Collection<Event> events = eventRepostiory.findAll();
        UserWeights userWeights = new UserWeights(events);
        BookWeights bookWeights = new BookWeights(votes, userWeights);

        List<Book> allBooks = bookRepository.findAll();
        Stream<Book> booksWithoutEvents = allBooks
                .stream()
                .filter(this::isSuggested);
        List<Book> preSortedBooks = booksWithoutEvents
                .sorted(new BookComparator(bookWeights))
                .toList();
        Stream<Book> sortedBooks = preSortedBooks
                .stream()
                .sorted(new RecommendationBenchComparator(preSortedBooks));
        return sortedBooks.map(new BookToBookAndSuggesterDtoConverter()::convert)
                .toList();
    }

    private boolean isSuggested(Book book) {
        boolean hasSuggestion = Objects.nonNull(book.getSuggestions()) && !book.getSuggestions().isEmpty();
        boolean hasRecommender = Objects.nonNull(book.getRecommenderExternalId());
        boolean hasNoEvent = Objects.isNull(book.getEvents()) || book.getEvents().isEmpty();
        return hasSuggestion || hasRecommender && hasNoEvent;
    }
}
