package hu.bmiklos.bc.business.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SaltedPasswordEncoder implements PasswordEncoder {
    private PasswordEncoder encoder;
    private String salt;

    public SaltedPasswordEncoder(String algorithm, String salt) {
        this.encoder = getEncoderBy(algorithm);
        this.salt = salt;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword + salt);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword + salt, encodedPassword);
    }

    private PasswordEncoder getEncoderBy(String algorithm) {
        switch (algorithm) {
            case "plaintext":
                return new PlainTextPasswordEncoder();
            case "bcrypt":
            default:
                return new BCryptPasswordEncoder();
        }
    }

    /**
     * Custom password encoder for passwords stored in plain text. To be used only for testing purposes.
     */
    private class PlainTextPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}
