package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.model.entity.SuggestionEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class SuggestionEntityMapper implements Converter<SuggestionEntity, Suggestion> {

  private final Book book;

  public SuggestionEntityMapper() {
    this.book = null;
  }

  @Nullable
  @Override
  public Suggestion convert(SuggestionEntity source) {
    ShallowUser suggester = new ShallowUserEntityMapper().convert(source.getSuggester());
    Book book =
        Optional.ofNullable(this.book)
            .orElseGet(() -> new BookEntityMapper().convert(source.getBook()));
    return new Suggestion(
        source.getId(), source.getCreationDate(), source.getDescription(), suggester, book);
  }
}
