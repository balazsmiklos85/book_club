package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Password;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.entity.PasswordEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class PasswordEntityMapper implements Converter<PasswordEntity, Password> {
  private final User targetUser;

  @Override
  public Password convert(PasswordEntity source) {
    Password result = new Password(source.getPasswordHash(), source.getSalt(), source.getHashAlgorithm());
    User user = Optional.ofNullable(targetUser).orElseGet(() -> new UserEntityMapper().convert(source.getUser()));
    result.setUser(user);
    return result;
  }
}
