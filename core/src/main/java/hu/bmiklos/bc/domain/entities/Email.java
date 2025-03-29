package hu.bmiklos.bc.domain.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Email {
  private final String emailAddress;
  /**
   * @deprecated There should be no circular references in the domain model.
   */
  @Deprecated
  private User user;

  @Override
  public String toString() {
    return emailAddress;
  }
}
