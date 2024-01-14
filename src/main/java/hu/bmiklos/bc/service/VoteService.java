package hu.bmiklos.bc.service;

import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;

public interface VoteService {

    void vote(UUID bookId);

    void unvote(UUID bookId);

    List<Vote> getUserVotes();

    List<BookAndSuggesterDto> getVotedBooks();

    List<List<String>> getMatrix();
}
