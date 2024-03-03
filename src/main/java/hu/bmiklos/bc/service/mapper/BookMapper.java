package hu.bmiklos.bc.service.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.service.BookAgeDeterminator;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.SuggestionDto;
import hu.bmiklos.bc.service.dto.UserDto;

/**
 * @deprecated This file should be moved to the controller level.
 */
@Deprecated(since="1.3.4", forRemoval=false)
public class BookMapper {
    private BookMapper() {}

    public static List<LeaderboardBookData> mapToLeaderboardBookData(List<BookAndSuggesterDto> books, List<BookAndSuggesterDto> userVotedBooks) {
        List<UUID> userVotedBookIds = userVotedBooks.stream()
            .map(BookAndSuggesterDto::book)
            .map(BookDto::getId)
            .toList();
        return books.stream()
            .map(book -> mapToLeaderboardBookData(book, userVotedBookIds))
            .toList();
    }

    private static LeaderboardBookData mapToLeaderboardBookData(BookAndSuggesterDto bookAndSuggester, List<UUID> userVotedBooks) {
        BookDto book = bookAndSuggester.book();
        List<SuggestionReference> suggestions = bookAndSuggester.suggestions()
            .stream()
            .map(SuggestionMapper::mapToReference)
            .toList();
        var hasher = new NormalizedSHA256HashGenerator();
        List<String> voters = bookAndSuggester.suggestions()
            .stream()
            .map(SuggestionDto::getSuggester)
            .map(UserDto::getEmails)
            .filter(CollectionUtils::isNotEmpty)
            .map(emails -> emails.stream()
                    .map(hasher::apply)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        boolean userVoted = userVotedBooks.contains(book.getId());
        return new LeaderboardBookData(book.getId(), book.getAuthor(),
                book.getTitle(), book.getUrl(), suggestions, voters, userVoted,
                new BookAgeDeterminator(bookAndSuggester).isFromTheLastMonth());
    }
}
