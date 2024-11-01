package hu.bmiklos.bc.domain.entities;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class User {
  private final UUID id;
  private final String name;
  private final boolean isAdmin;
  private final int externalId;
  private final Password password;
  private final List<Email> emails;
}
