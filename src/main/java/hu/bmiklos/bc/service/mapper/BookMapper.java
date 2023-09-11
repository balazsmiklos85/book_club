package hu.bmiklos.bc.service.mapper;

import java.util.List;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.dto.BookDto;

public class BookMapper {
    private BookMapper() {}

    public static BookDto mapToDto(Book book) {
        return new BookDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(), book.getRecommenderExternalId()));
    }

    public static List<LeaderboardBookData> mapToLeaderboardBookData(List<BookDto> books, List<BookDto> userVotedBooks) {
        return books.stream()
            .map(book -> mapToLeaderboardBookData(book, userVotedBooks))
            .toList();
    }

    private static LeaderboardBookData mapToLeaderboardBookData(BookDto book, List<BookDto> userVotedBooks) {
        boolean userVoted = userVotedBooks.contains(book);
        return new LeaderboardBookData(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), book.getRecommender(), userVoted);
    }
}
