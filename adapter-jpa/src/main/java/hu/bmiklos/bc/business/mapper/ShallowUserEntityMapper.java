package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.ShallowUser;
import hu.bmiklos.bc.model.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class ShallowUserEntityMapper implements Converter<UserEntity, ShallowUser> {

  @Override
  public ShallowUser convert(UserEntity source) {
    return new ShallowUser(
        source.getId(),
        source.getName(),
        source.isAdmin(),
        source.getExternalId(),
        source.getEmails().stream().map(new EmailEntityMapper()::convert).toList());
  }
}
