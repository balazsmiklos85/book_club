package hu.bmiklos.bc.domain.entities;

import hu.bmiklos.bc.domain.functions.ExternalIdFilter;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

@Data
@ToString(onlyExplicitlyIncluded = true)
public class Book {
  private final UUID id;
  @ToString.Include
  private final String author;
  @ToString.Include
  private final String title;
  private final String url;

  /**
   * @deprecated There should be no circular dependencies in the domain layer.
   */
  @Deprecated private final Collection<Suggestion> suggestions;

    /**
     * @deprecated There should be no circular dependencies in the domain layer.
     */
  @Deprecated private final Collection<Vote> voters;

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
    this.suggestions = new ArrayList<>(CollectionUtils.emptyIfNull(suggestionProvider.apply(this)));
    this.voters = new ArrayList<>(CollectionUtils.emptyIfNull(voteProvider.apply(this)));
  }

    public boolean isUserVoted(Member member) {
    return Optional.ofNullable(member)
        .map(Member::externalId)
        .map(ExternalIdFilter::new)
        .map(voters.stream().map(Vote::getUser).map(Member::externalId)::anyMatch)
        .orElse(false);
  }
}

