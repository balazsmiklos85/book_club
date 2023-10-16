package hu.bmiklos.bc.service.mapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.BookAgeDeterminator;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class BookMapper {
    private BookMapper() {}

    public static BookDto mapToDto(Book book) {
        List<SuggestionDto> suggesters;
        if (book.getSuggestions() == null || book.getSuggestions().isEmpty()) {
            if (book.getRecommender() == null) {
                return new BookDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), book.getRecommenderExternalId());
            }
            suggesters =  List.of(new SuggestionDto(book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(), book.getRecommenderExternalId())));
        } else {
            suggesters = SuggestionMapper.mapToDto(book.getSuggestions());
        }
        return new BookDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters);
    }

    public static List<LeaderboardBookData> mapToLeaderboardBookData(List<BookDto> books, List<BookDto> userVotedBooks) {
        return books.stream()
            .map(book -> mapToLeaderboardBookData(book, userVotedBooks))
            .toList();
    }

    private static LeaderboardBookData mapToLeaderboardBookData(BookDto book, List<BookDto> userVotedBooks) {
        List<String> suggesters;
        if (book.getHistoricSuggester().isPresent()) {
            suggesters = List.of("[" + book.getHistoricSuggester().get() + "]");
        } else {
            suggesters = book.getSuggesters()
                .stream()
                .map(SuggestionDto::getSuggester)
                .map(UserDto::getName)
                .toList();
        }
        boolean userVoted = userVotedBooks.contains(book);
        LeaderboardBookData result = new LeaderboardBookData(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters, userVoted);
        result.setNew(new BookAgeDeterminator(book).isFromTheLastMonth());
        return result;
    }
}
