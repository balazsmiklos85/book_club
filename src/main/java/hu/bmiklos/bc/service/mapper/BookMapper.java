package hu.bmiklos.bc.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.model.Book;
import hu.bmiklos.bc.service.BookAgeDeterminator;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;

public class BookMapper {
    private BookMapper() {}

    public static BookAndSuggesterDto mapToDto(Book book) {
        final List<SuggestionDto> suggesters = new ArrayList<>(1);
        if (book.getSuggestions() == null || book.getSuggestions().isEmpty() && book.getRecommender() == null) {
            return new BookAndSuggesterDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(),
                    book.getRecommenderExternalId());
        }
        if (book.getRecommender() != null) {
            var suggester = new SuggestionDto(book.getRecommendedAt(), UserMapper.mapToDto(book.getRecommender(),
                    book.getRecommenderExternalId()), "");
            suggesters.add(suggester);
        }
        if (book.getSuggestions() != null && !book.getSuggestions().isEmpty()) {
            suggesters.addAll(SuggestionMapper.mapToDto(book.getSuggestions()));
        }
        return new BookAndSuggesterDto(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggesters);
    }

    public static List<LeaderboardBookData> mapToLeaderboardBookData(List<BookAndSuggesterDto> books, List<BookAndSuggesterDto> userVotedBooks) {
        List<UUID> userVotedBookIds = userVotedBooks.stream()
            .map(book -> book.getBook())
            .map(BookDto::getId)
            .toList();
        return books.stream()
            .map(book -> mapToLeaderboardBookData(book, userVotedBookIds))
            .toList();
    }

    private static LeaderboardBookData mapToLeaderboardBookData(BookAndSuggesterDto bookAndSuggester, List<UUID> userVotedBooks) {
        BookDto book = bookAndSuggester.getBook();
        List<SuggestionReference> suggestions;
        if (bookAndSuggester.getHistoricSuggester().isPresent()) {
            suggestions = List.of("[" + bookAndSuggester.getHistoricSuggester().get() + "]").stream()
                .map(SuggestionReference::new)
                .toList();
        } else {
            suggestions = bookAndSuggester.getSuggesters()
                .stream()
                .map(SuggestionMapper::mapToReference)
                .toList();
        }
        boolean userVoted = userVotedBooks.contains(book.getId());
        LeaderboardBookData result = new LeaderboardBookData(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggestions, userVoted);
        result.setNew(new BookAgeDeterminator(bookAndSuggester).isFromTheLastMonth());
        return result;
    }
}
