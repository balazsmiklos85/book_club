package hu.bmiklos.bc.domain.entities;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;

import lombok.Data;

@Data
public class Book {
  private final UUID id;
  private final String author;
  private final String title;
  private final String url;
  private final Collection<Suggestion> suggestions;
  private final Collection<Vote> voters;

  public Book(
      UUID id,
      String author,
      String title,
      String url,
      Function<Book, Collection<Suggestion>> suggestionProvider,
      Function<Book, Collection<Vote>> voteProvider) {
    this.id = id;
    this.author = author;
    this.title = title;
    this.url = url;
    this.suggestions = suggestionProvider.apply(this);
    this.voters = voteProvider.apply(this);
  }

public boolean isFromTheLastMonth() {
    return suggestions.stream().anyMatch(Suggestion::isFromTheLastMonth);
  }

  public boolean isUserVoted(User user) {
    if (isNull(user)) {
      return false;
    }
    ShallowUser userToCheck = user.shallow();
    return voters.stream().map(Vote::getUser).anyMatch(userToCheck::equals);
  }
}
