package hu.bmiklos.bc.controller.mapper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class LeaderboardBookElementsConverter implements
    Converter<Collection<BookAndSuggesterDto>, Collection<LeaderboardBookData>> {

    private final Set<UUID> userVotedBookIds;
    private final Map<UUID, Collection<UserDto>> votersByBooks;

    public LeaderboardBookElementsConverter(final Collection<BookAndSuggesterDto> userVotedBooks, final Map<UUID, Collection<UserDto>> votersByBooks) {
        this.userVotedBookIds = userVotedBooks.stream()
            .map(BookAndSuggesterDto::book)
            .map(BookDto::getId)
            .collect(Collectors.toSet());
        this.votersByBooks = votersByBooks;
    }

    @Override
    @NonNull
    public Collection<LeaderboardBookData> convert(final Collection<BookAndSuggesterDto> source) {
        final var bookConverter = new LeaderboardElementConverter(userVotedBookIds, votersByBooks);
        return source.stream()
            .map(bookConverter::convert)
            .toList();
    }
}
