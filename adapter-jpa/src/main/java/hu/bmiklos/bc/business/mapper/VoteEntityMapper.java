package hu.bmiklos.bc.business.mapper;

import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.model.entity.VoteEntity;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VoteEntityMapper implements Converter<VoteEntity, Vote> {
  private final Book book;

  @Override
  public Vote convert(VoteEntity source) {
    final ShallowUser user = Stream.of(source.getUserById(), source.getUserByExternalId())
        .filter(Objects::nonNull)
        .map(u -> new ShallowUserEntityMapper().convert(u))
        .findFirst()
        .orElseGet(() -> new ShallowUser(null, "[" + source.getUserExternalId() + "]", false, source.getUserExternalId(), null));
    return new Vote(source.getId(), book, user);
  }
}
