package hu.bmiklos.bc.util;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class SaltGenerator {

    private int length;
    private Random random;

    public SaltGenerator() {
        this(16);
    }

    public SaltGenerator(int length) {
        this.length = length;
        this.random = new SecureRandom();
    }

    public String generateSalt() {
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
