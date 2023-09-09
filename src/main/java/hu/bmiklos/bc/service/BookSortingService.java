package hu.bmiklos.bc.service;

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
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.mapper.BookMapper;

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

    public List<BookDto> getAll() {
        List<Vote> votes = voteRepository.findAll();
        List<Event> events = eventRepostiory.findAll();
        UserWeights userWeights = new UserWeights(events);
        BookWeights bookWeights = new BookWeights(votes, userWeights);

        List<Book> allBooks = bookRepository.findAll();
        Stream<Book> booksWithoutEvents = allBooks
                .stream()
                .filter(this::hasNoEvents);
        Stream<Book> sortedBooks = booksWithoutEvents
                .sorted(new BookComparator(bookWeights));
        return sortedBooks.map(BookMapper::mapToDto)
                .toList();
    }

    private boolean hasNoEvents(Book book) {
        return Objects.isNull(book.getEvents()) || book.getEvents().isEmpty();
    }
}
