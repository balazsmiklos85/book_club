package hu.bmiklos.bc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.repository.VoteRepository;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.mapper.BookMapper;
import jakarta.transaction.Transactional;

@Service
public class VoteServiceImpl extends AuthenticatedService  implements VoteService {
    private final ActiveUserService activeUserService;

    private final VoteRepository voteRepository;

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
        Optional<Vote> byBookIdAndUserExternalId = voteRepository.findByBookIdAndUserExternalId(bookId, getExternalUserId());
        if (byBookIdAndUserId.isPresent() || byBookIdAndUserExternalId.isPresent()) {
            return;
        }
            
        var vote = new Vote(bookId, getUserId());
        voteRepository.saveAndFlush(vote);
    }

    @Override
    public List<BookDto> getVotedBooks() {
        List<Vote> userVotes = voteRepository.findByUserId(activeUserService.getUserId());
        return userVotes.stream()
            .map(Vote::getBook)
            .map(BookMapper::mapToDto)
            .toList();
    }
}
