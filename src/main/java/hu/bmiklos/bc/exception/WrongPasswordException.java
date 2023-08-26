package hu.bmiklos.bc.exception;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("Authentication failed: wrong password.");
    }
}
