package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Book;
import hu.bmiklos.bc.domain.entities.Vote;
import hu.bmiklos.bc.model.entity.UserEntity;
import hu.bmiklos.bc.model.entity.VoteEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class VoteEntityMapper implements Converter<VoteEntity, Vote> {
  private final Book book;

  @Override
  public Vote convert(VoteEntity source) {
    final UserEntity user =
        Optional.ofNullable(source.getUserById()).orElseGet(source::getUserByExternalId);
    return new Vote(source.getId(), book, new ShallowUserEntityMapper().convert(user));
  }
}
