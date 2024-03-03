package hu.bmiklos.bc.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NormalizedSHA256HashGeneratorTest {
    private NormalizedSHA256HashGenerator hasher;

    @BeforeEach
    void setup() {
        hasher = new NormalizedSHA256HashGenerator();
    }

    @Test
    void generatesHash() {
        Optional<String> hash = hasher.apply("input string");

        assertTrue(hash.isPresent(),
                "The hasher should generate a SHA256 hash.");
        assertEquals(
                "f23f4781d6814ebe349c6b230c1f700714f4f70f735022bd4b1fb69421859993",
                hash.get(),
                "The hasher should generate the right SHA256 hash.");
    }

    @Test
    void trimsAndConvertsToLowerCaseBeforeHashing() {
        Optional<String> hash = hasher.apply("   InPuT sTrInG               ");

        assertTrue(hash.isPresent(),
                "The hasher should generate a SHA256 hash.");
        assertEquals(
                "f23f4781d6814ebe349c6b230c1f700714f4f70f735022bd4b1fb69421859993",
                hash.get(),
                "The hasher should generate the right SHA256 hash.");
    }

    @Test
    void handlesNullInput() {
        Optional<String> hash = hasher.apply(null);

        assertTrue(hash.isEmpty(), "There should be no hash for a null input.");
    }

    @Test
    void handlesNonExistingAlgorithm() {
        hasher = new NormalizedSHA256HashGenerator("non-existing algorithm");

        Optional<String> hash = hasher.apply("input string");

        assertTrue(hash.isEmpty(),
                "There should be no hash with a wrong algorithm.");
    }
}
