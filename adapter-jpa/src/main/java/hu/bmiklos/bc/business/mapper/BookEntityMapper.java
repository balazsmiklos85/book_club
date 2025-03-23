package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.model.entity.BookEntity;
import java.util.Collection;
import java.util.function.Function;
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
            source.getSuggestions().stream()
                .map(new SuggestionEntityMapper(book)::convert)
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
}
