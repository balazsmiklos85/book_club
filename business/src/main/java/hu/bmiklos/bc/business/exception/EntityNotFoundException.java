package hu.bmiklos.bc.business.exception;

import java.text.MessageFormat;

public class EntityNotFoundException extends RuntimeException {
  public EntityNotFoundException(Object id) {
    super(MessageFormat.format("Entity with id ''{0}'' not found.", id));
  }
}
