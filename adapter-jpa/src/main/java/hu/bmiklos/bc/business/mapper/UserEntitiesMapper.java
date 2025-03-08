package hu.bmiklos.bc.business.mapper;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.entity.UserEntity;
import java.util.Collection;
import org.springframework.core.convert.converter.Converter;

public class UserEntitiesMapper implements Converter<Collection<UserEntity>, Collection<User>> {
  @Override
  public Collection<User> convert(Collection<UserEntity> source) {
    UserEntityMapper mapper = new UserEntityMapper();
    return emptyIfNull(source).stream().map(mapper::convert).toList();
  }
}
