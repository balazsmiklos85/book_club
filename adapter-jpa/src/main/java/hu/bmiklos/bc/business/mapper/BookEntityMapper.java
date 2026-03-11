package hu.bmiklos.bc.business.mapper;

import static java.util.Objects.isNull;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.model.entity.BookEntity;
import hu.bmiklos.bc.model.entity.UserEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class BookEntityMapper implements Converter<BookEntity, Book> {

  @Nullable
  @Override
  public Book convert(BookEntity source) {
    final Function<Book, Collection<Suggestion>> suggestionProvider =
        book ->
            Stream.concat(convertSuggestions(source, book), convertLegacySuggestion(source, book))
                .toList();
    final Function<Book, Collection<Vote>> voteProvider =
        book -> source.getVotes().stream().map(new VoteEntityMapper(book)::convert).toList();
    return new Book(
        source.getId(),
        source.getAuthor(),
        source.getTitle(),
        source.getUrl(),
        suggestionProvider,
        voteProvider);
  }

  public Stream<Suggestion> convertSuggestions(BookEntity source, Book book) {
    return source.getSuggestions().stream().map(new SuggestionEntityMapper(book)::convert);
  }

  public Stream<Suggestion> convertLegacySuggestion(BookEntity source, Book book) {
    UserEntity legacyRecommender = source.getRecommender();
    Integer legacyRecommenderExtId = source.getRecommenderExternalId();
    Instant legacyRecommendedAt = source.getRecommendedAt();
    if (isNull(legacyRecommender) && isNull(legacyRecommenderExtId)) {
      return Stream.empty();
    }
    if (isNull(legacyRecommender)) {
      var suggester = new ShallowUser(null, null, false, legacyRecommenderExtId, List.of());
      return Stream.of(new Suggestion(null, legacyRecommendedAt, "", suggester, book));
    }
    var suggester = new ShallowUserEntityMapper().convert(legacyRecommender);
    return Stream.of(new Suggestion(null, legacyRecommendedAt, "", suggester, book));
  }
}
