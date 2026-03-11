package hu.bmiklos.bc.service.mapper;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class BookToBookDtoConverter implements Converter<Book, BookDto> {

  @Override
  @NonNull
  public BookDto convert(Book source) {
    return new BookDto(source.getId(), source.getAuthor(), source.getTitle(), source.getUrl());
  }
}
