package hu.bmiklos.bc.service;

import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.model.Vote;

public interface VoteService {

    void vote(UUID bookId);

    void unvote(UUID bookId);

    List<Vote> getUserVotes();
}
