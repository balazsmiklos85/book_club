package hu.bmiklos.bc.domain.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Password {
  private User user;
  private final String passwordHash;
  private final String salt;
  private final String hashAlgorithm; // TODO should be an enum
}
