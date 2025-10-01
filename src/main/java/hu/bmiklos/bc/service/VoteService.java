package hu.bmiklos.bc.service;

import hu.bmiklos.bc.model.Vote;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface VoteService {

  void vote(UUID bookId);

  void unvote(UUID bookId);

  List<Vote> getUserVotes();

  List<BookAndSuggesterDto> getVotedBooks();

  List<List<String>> getMatrix();

  Map<UUID, Collection<UserDto>> getAllVotersByBooks();
}
