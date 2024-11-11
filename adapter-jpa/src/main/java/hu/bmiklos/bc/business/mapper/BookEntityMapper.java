package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.model.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class BookEntityMapper implements Converter<BookEntity, Book> {

  @Nullable
  @Override
  public Book convert(BookEntity source) {
    return new Book(source.getId(), source.getAuthor(), source.getTitle(), source.getUrl());
  }
}
