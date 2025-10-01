package hu.bmiklos.bc.web.mapper;

import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Vote;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class VoterHasher implements Converter<Vote, Optional<String>> {

  private final NormalizedHashGenerator hasher;

  public VoterHasher() {
    this.hasher = new NormalizedHashGenerator();
  }

  @Override
  @NonNull
  public Optional<String> convert(final Vote source) {
    return Optional.ofNullable(source)
        .map(Vote::getUser)
        .map(ShallowUser::emails)
        .map(Collection::stream)
        .orElse(Stream.empty())
        .map(Email::toString)
        .map(hasher::apply)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
}
