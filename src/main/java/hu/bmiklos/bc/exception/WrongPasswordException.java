package hu.bmiklos.bc.exception;

import org.springframework.security.core.AuthenticationException;

public class WrongPasswordException extends AuthenticationException {
    public WrongPasswordException() {
        super("Authentication failed: wrong password.");
    }
}
