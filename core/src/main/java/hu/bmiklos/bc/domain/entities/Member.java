package hu.bmiklos.bc.domain.entities;

import static java.util.Objects.nonNull;

public interface Member {
  Integer externalId();

  default boolean isSameAs(Member member) {
    return nonNull(externalId())
      
        && nonNull(member)
        && nonNull(member.externalId())
        && externalId().equals(member.externalId());
  }
}
