package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.entity.SuggestionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class SuggestionEntityMapper implements Converter<SuggestionEntity, Suggestion> {

  @Nullable
  @Override
  public Suggestion convert(SuggestionEntity source) {
    User suggester = new UserEntityMapper().convert(source.getSuggester());
    Book book = new BookEntityMapper().convert(source.getBook());
    return new Suggestion(source.getId(), source.getCreationDate(), source.getDescription(), suggester, book);
  }
}
