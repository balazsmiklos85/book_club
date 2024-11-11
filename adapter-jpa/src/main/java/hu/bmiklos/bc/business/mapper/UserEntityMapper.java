package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Password;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.entity.UserEntity;
import org.springframework.core.convert.converter.Converter;

public class UserEntityMapper implements Converter<UserEntity, User> {

  @Override
  public User convert(UserEntity source) {
    User result = new User(source.getId(), source.getName(), source.isAdmin(), source.getExternalId());
    Password password = new PasswordEntityMapper(result).convert(source.getPassword());
    result.setPassword(password);
    EmailEntityMapper emailEntityMapper = new EmailEntityMapper(result);
    result.setEmails(source.getEmails().stream().map(emailEntityMapper::convert).toList());
    return result;
  }
}
