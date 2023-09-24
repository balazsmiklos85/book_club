package hu.bmiklos.bc.service.mapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        String recommender = book.getRecommender().getName();
        if (recommender == null || recommender.isBlank()) {
            recommender = "[" + book.getRecommender().getExternalId() + "]";
        }
        LeaderboardBookData result = new LeaderboardBookData(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), recommender, userVoted);
        boolean isFromTheLastMonth = book.getRecommendedAt().isAfter(Instant.now().minus(30l, ChronoUnit.DAYS));
        result.setNew(isFromTheLastMonth);
        return result;
    }
}
