package hu.bmiklos.bc.service.mapper;

import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookDto;

public class BookMapper {
    private BookMapper() {}

    public static BookDto mapToDto(Book book) {
        return new BookDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender()));
    }
}
