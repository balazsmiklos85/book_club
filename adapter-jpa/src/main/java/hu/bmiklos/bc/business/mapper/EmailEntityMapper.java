package hu.bmiklos.bc.business.mapper;

import hu.bmiklos.bc.domain.entities.Email;
import hu.bmiklos.bc.domain.entities.User;
import hu.bmiklos.bc.model.entity.EmailEntity;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;

@RequiredArgsConstructor
public class EmailEntityMapper implements Converter<EmailEntity, Email> {

  private final User targetUser;

  public EmailEntityMapper() {
    this.targetUser = null;
  }

  @Override
  public Email convert(EmailEntity source) {
    Email result = new Email(source.getEmailAddress());
    User user = Optional.ofNullable(this.targetUser)
        .orElseGet(() -> new UserEntityMapper().convert(source.getUser()));
    result.setUser(user);
    return result;
  }
}
