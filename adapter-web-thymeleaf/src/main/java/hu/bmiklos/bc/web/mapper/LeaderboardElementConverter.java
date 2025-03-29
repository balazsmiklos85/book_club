package hu.bmiklos.bc.web.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Suggestion;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.web.dto.LeaderboardBookData;
import hu.bmiklos.bc.web.dto.SuggestionReference;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class LeaderboardElementConverter implements Converter<Book, LeaderboardBookData> {
  private final User user;

  @Override
  public LeaderboardBookData convert(final Book source) {
    final var suggestionConverter = new SuggestionReferenceConverter();
    final Collection<SuggestionReference> suggestions =
        source.getSuggestions().stream().map(suggestionConverter::convert).toList();
    final var voterHasher = new VoterHasher();
    final Collection<String> voterHashes =
        source.getVoters().stream()
            .map(voterHasher::convert)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    return new LeaderboardBookData(
        source.getId(),
        source.getAuthor(),
        source.getTitle(),
        source.getUrl(),
        suggestions,
        voterHashes,
        source.isUserVoted(user),
        source.getSuggestions().stream().anyMatch(Suggestion::isFromTheLastMonth));
  }
}
