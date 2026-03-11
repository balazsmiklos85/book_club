package hu.bmiklos.bc.business.exception;

public class DifferentUserException extends RuntimeException {
  public DifferentUserException() {
    super("Resource belongs to a different user.");
  }
}
