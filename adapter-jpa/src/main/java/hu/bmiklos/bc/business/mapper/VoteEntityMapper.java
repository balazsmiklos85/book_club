package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.model.entity.VoteEntity;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class VoteEntityMapper implements Converter<VoteEntity, Vote> {
  private final Book book;

  @Override
  public Vote convert(VoteEntity source) {
    final ShallowUser user =
        Stream.of(source.getUserById(), source.getUserByExternalId())
            .filter(Objects::nonNull)
            .map(u -> new ShallowUserEntityMapper().convert(u))
            .findFirst()
            .orElseGet(
                () ->
                    new ShallowUser(
                        null,
                        "[" + source.getUserExternalId() + "]",
                        false,
                        source.getUserExternalId(),
                        null));
    return new Vote(source.getId(), book, user);
  }
}
