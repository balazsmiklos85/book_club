package hu.bmiklos.bc.service.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SaltedPasswordEncoderTest {
    @Test
    void encodeSaltsBeforeHashing() {
        var encoder = new SaltedPasswordEncoder("plaintext", "salt");
        
        String hash = encoder.encode("password");
        
        assertEquals("passwordsalt", hash, "The password should be salted before hashing.");
    }

    @Test
    void matchesSaltsBeforeChecking() {
        var encoder = new SaltedPasswordEncoder("plaintext", "salt");
        
        boolean matches = encoder.matches("password", "passwordsalt");
        
        assertTrue(matches, "The password should be salted before checking.");
    }
}
