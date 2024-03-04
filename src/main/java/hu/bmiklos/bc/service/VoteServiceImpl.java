package hu.bmiklos.bc.service;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.model.Event;
import hu.bmiklos.bc.model.User;
import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.repository.BookRepository;
import hu.bmiklos.bc.repository.EventRepository;
import hu.bmiklos.bc.repository.UserRepository;
import hu.bmiklos.bc.repository.VoteRepository;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.UserDto;
import hu.bmiklos.bc.service.mapper.BookToBookAndSuggesterDtoConverter;
import hu.bmiklos.bc.service.mapper.UserMapper;
import jakarta.transaction.Transactional;

@Service
public class VoteServiceImpl extends AuthenticatedService implements VoteService {
    private final ActiveUserService activeUserService;

    private final VoteRepository voteRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepostiory;

    public VoteServiceImpl(ActiveUserService activeUserService, VoteRepository voteRepository) {
        this.activeUserService = activeUserService;
        this.voteRepository = voteRepository;
    }

    @Override
    public List<Vote> getUserVotes() {
        var result = new ArrayList<Vote>();
        result.addAll(voteRepository.findByUserId(getUserId()));
        result.addAll(voteRepository.findByUserExternalId(getExternalUserId()));
        return result;
    }

    @Override
    @Transactional
    public void unvote(UUID bookId) {
        voteRepository.deleteByBookIdAndUserId(bookId, getUserId());
        voteRepository.deleteByBookIdAndUserExternalId(bookId, getExternalUserId());
    }

    @Override
    @Transactional
    public void vote(UUID bookId) {
        Optional<Vote> byBookIdAndUserId = voteRepository.findByBookIdAndUserId(bookId, getUserId());
        Optional<Vote> byBookIdAndUserExternalId = voteRepository.findByBookIdAndUserExternalId(bookId,
                getExternalUserId());
        if (byBookIdAndUserId.isPresent() || byBookIdAndUserExternalId.isPresent()) {
            return;
        }

        var vote = new Vote(bookId, getUserId());
        voteRepository.saveAndFlush(vote);
    }

    @Override
    public List<BookAndSuggesterDto> getVotedBooks() {
        List<Vote> votesByUserId = voteRepository.findByUserId(activeUserService.getUserId());
        List<Vote> votesByUserExternalId = voteRepository.findByUserExternalId(activeUserService.getExternalUserId());
        List<Vote> userVotes = new ArrayList<>(votesByUserId.size() + votesByUserExternalId.size());
        userVotes.addAll(votesByUserId);
        userVotes.addAll(votesByUserExternalId);
        return userVotes.stream()
                .map(Vote::getBook)
                .map(new BookToBookAndSuggesterDtoConverter()::convert)
                .toList();
    }

    @Override
    public List<List<String>> getMatrix() {
        List<Vote> allVotes = voteRepository.findAll();
        List<Book> allBooks = bookRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        List<Event> allEvents = eventRepostiory.findAll();
        UserWeights userWeights = new UserWeights(allEvents);
        Set<Integer> externalUsers = allVotes.stream()
                .map(Vote::getUserExternalId)
                .filter(Objects::nonNull)
                .filter(externalId -> allUsers.stream().noneMatch(user -> Objects.equals(user.getExternalId(), externalId)))
                .collect(Collectors.toSet());

        List<List<String>> result = new ArrayList<>();
        List<String> header = getUserNames(allUsers, externalUsers);
        result.add(header);
        List<String> weights = getUserWeights(allUsers, externalUsers, userWeights);
        result.add(weights);
        for (Book book : allBooks) {
            if (book.getEvents() != null && !book.getEvents().isEmpty()) {
                continue;
            }
            List<String> bookLine = getBookVotes(book, allUsers, externalUsers, allVotes, userWeights);
            result.add(bookLine);
        }
        return result;
    }

    private List<String> getUserWeights(List<User> allUsers, Set<Integer> externalUsers, UserWeights userWeights) {
        var result = new ArrayList<String>();
        result.add("");
        for (User user : allUsers) {
            result.add(userWeights.getWeight(user) + "");
        }
        for (Integer externalUser : externalUsers) {
            result.add(userWeights.getWeight(externalUser) + "");
        }
        return result;
    }

    private List<String> getBookVotes(Book book, List<User> allUsers, Collection<Integer> externalUsers, List<Vote> allVotes, UserWeights userWeights) {
        var result = new ArrayList<String>();
        result.add(book.getTitle());
        int sum = 0;
        for (User user : allUsers) {
            boolean voted = allVotes.stream().anyMatch(vote -> isVoteForBook(vote, book) && isVoteOfUser(vote, user));
            result.add(voted ? "1" : "0");
            if (voted) {
                sum += userWeights.getWeight(user);
            }
        }
        for (Integer externalUser : externalUsers) {
            boolean voted = allVotes.stream().anyMatch(vote -> isVoteForBook(vote, book) && Objects.equals(vote.getUserExternalId(), externalUser));
            result.add(voted ? "1" : "0");
            if (voted) {
                sum += userWeights.getWeight(externalUser);
            }
        }
        result.add(sum + "");
        return result;
    }

    private boolean isVoteOfUser(Vote vote, User user) {
        return Objects.equals(vote.getUserId(), user.getId())
            || Objects.equals(vote.getUserExternalId(), user.getExternalId());
    }

    private boolean isVoteForBook(Vote vote, Book book) {
        return Objects.equals(vote.getBookId(), book.getId());
    }

    private List<String> getUserNames(List<User> allUsers, Collection<Integer> externalUsers) {
        return Stream.of(List.of(""), allUsers.stream()
                .map(this::getUserNameOrExternalId)
                .collect(Collectors.toList()), externalUsers.stream().map(Object::toString).collect(Collectors.toList()), List.of("Sum"))
            .flatMap(List::stream)
            .toList();
    }

    private String getUserNameOrExternalId(User user) {
        return Stream.of(user.getName(), user.getExternalId())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .findFirst()
                .orElse("");
    }

    @Override
    public Map<UUID, Collection<UserDto>> getAllVotersByBooks() {
        List<Vote> votes = voteRepository.findAll();
        return votes.stream()
            .collect(new VoteByBookCollector());
    }
}
