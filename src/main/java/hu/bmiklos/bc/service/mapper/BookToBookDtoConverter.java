package hu.bmiklos.bc.service.mapper;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookDto;

public class BookToBookDtoConverter implements Converter<Book, BookDto> {

    @Override
    @NonNull
    public BookDto convert(Book source) {
        return new BookDto(source.getId(), source.getAuthor(),
                source.getTitle(), source.getUrl());
    }
}
