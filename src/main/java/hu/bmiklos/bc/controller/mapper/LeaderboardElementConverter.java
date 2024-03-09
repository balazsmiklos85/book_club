package hu.bmiklos.bc.controller.mapper;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import hu.bmiklos.bc.controller.dto.LeaderboardBookData;
import hu.bmiklos.bc.controller.dto.SuggestionReference;
import hu.bmiklos.bc.service.BookAgeDeterminator;
import hu.bmiklos.bc.service.dto.BookAndSuggesterDto;
import hu.bmiklos.bc.service.dto.BookDto;
import hu.bmiklos.bc.service.dto.UserDto;

public class LeaderboardElementConverter
    implements Converter<BookAndSuggesterDto, LeaderboardBookData> {

    private final Collection<UUID> userVotedBookIds;
    private final Map<UUID, Collection<UserDto>> votersByBookId;

    public LeaderboardElementConverter(
            @NonNull final Collection<UUID> userVotedBookIds,
            @NonNull final Map<UUID, Collection<UserDto>> votersByBookId) {
        this.userVotedBookIds = userVotedBookIds;
        this.votersByBookId = votersByBookId;
    }

    @Override
    public LeaderboardBookData convert(final BookAndSuggesterDto source) {
        final BookDto book = source.book();
        final Collection<SuggestionReference> suggestions = new SuggestionsConverter()
            .convert(source.suggestions());
        Collection<UserDto> voters = CollectionUtils.emptyIfNull(
                votersByBookId.get(book.getId()));
        final Collection<String> voterHashes = new VotersHasher()
            .convert(voters);
        final boolean userVoted = userVotedBookIds.contains(book.getId());
        final var bookAgeDeterminator = new BookAgeDeterminator(source);
        return new LeaderboardBookData(book.getId(), book.getAuthor(),
                book.getTitle(), book.getUrl(), suggestions, voterHashes,
                userVoted, bookAgeDeterminator.isFromTheLastMonth());
    }
}
