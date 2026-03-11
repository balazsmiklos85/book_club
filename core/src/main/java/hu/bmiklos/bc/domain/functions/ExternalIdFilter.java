package hu.bmiklos.bc.domain.functions;

import java.util.Objects;
import java.util.function.Predicate;

public class ExternalIdFilter implements Predicate<Integer> {

  private Integer id;

  public ExternalIdFilter(Integer externalId) {
    this.id = externalId;
  }

  @Override
  public boolean test(Integer otherId) {
    return Objects.nonNull(id) && Objects.nonNull(otherId) && Objects.equals(id, otherId);
  }
}
