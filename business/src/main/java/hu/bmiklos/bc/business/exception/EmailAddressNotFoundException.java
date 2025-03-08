package hu.bmiklos.bc.business.exception;

public class EmailAddressNotFoundException extends RuntimeException {

        public EmailAddressNotFoundException(String emailAddress) {
            super("Email address not found: " + emailAddress);
        }
}
