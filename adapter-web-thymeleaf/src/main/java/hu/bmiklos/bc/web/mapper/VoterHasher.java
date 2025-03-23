package hu.bmiklos.bc.web.mapper;

import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.Vote;
import java.util.Optional;
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
    return source.getUser().emails().stream()
        .map(Email::toString)
        .map(hasher::apply)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }
}
