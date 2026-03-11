package hu.bmiklos.bc.domain.entities;

import static java.lang.Boolean.TRUE;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@Data
@RequiredArgsConstructor
public class User implements Member {
  private final UUID id;
  private final String name;
  private final Boolean isAdmin;
  private final Integer externalId;
  private Password password;
  private List<Email> emails;

  public boolean isAdmin() {
    return TRUE.equals(isAdmin);
  }

  public ShallowUser shallow() {
    return new ShallowUser(
        id, name, isAdmin, externalId, new ArrayList<Email>(CollectionUtils.emptyIfNull(emails)));
  }

  @Override
  public Integer externalId() {
    return externalId;
  }
}
