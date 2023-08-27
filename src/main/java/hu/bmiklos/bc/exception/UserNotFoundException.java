package hu.bmiklos.bc.exception;

public class UserNotFoundException extends RuntimeException {

        public UserNotFoundException(String username) {
            super("User not found: " + username);
        }
}
