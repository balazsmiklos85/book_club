package hu.bmiklos.bc.service.mapper;

import java.util.List;
import java.util.UUID;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.service.BookAgeDeterminator;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;

/**
 * @deprecated This file should be moved to the controller level.
 */
@Deprecated(since="1.3.4", forRemoval=false)
public class BookMapper {
    private BookMapper() {}

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
        List<SuggestionReference> suggestions = bookAndSuggester.getSuggesters()
            .stream()
            .map(SuggestionMapper::mapToReference)
            .toList();
        boolean userVoted = userVotedBooks.contains(book.getId());
        LeaderboardBookData result = new LeaderboardBookData(book.getId(), book.getAuthor(), book.getTitle(), book.getUrl(), suggestions, userVoted);
        result.setNew(new BookAgeDeterminator(bookAndSuggester).isFromTheLastMonth());
        return result;
    }
}
