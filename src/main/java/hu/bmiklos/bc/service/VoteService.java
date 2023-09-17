package hu.bmiklos.bc.service;

import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.dto.BookDto;

public interface VoteService {

    void vote(UUID bookId);

    void unvote(UUID bookId);

    List<Vote> getUserVotes();

    List<BookDto> getVotedBooks();

    List<List<String>> getMatrix();
}
