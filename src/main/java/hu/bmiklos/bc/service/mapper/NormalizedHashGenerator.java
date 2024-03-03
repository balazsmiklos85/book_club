package hu.bmiklos.bc.service.mapper;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Generates a hash of a provided String. This class performs additional
 * processing on the input string before hashing:
 * - Trims leading and trailing whitespace characters.
 * - Converts the String to lowercase using the default Locale.
 *
 * The resulting hash is represented as a lowercase hexadecimal String.
 * If the input String is null, an empty {@link Optional} is returned.
 */
public class NormalizedHashGenerator implements Function<String,
       Optional<String>> {

    private final MessageDigest messageDigest;

    /**
     * Constructs a new NormalizedHashGenerator instance. This constructor
     * initializes the message digest algorithm to "SHA-256".
     */
    public NormalizedHashGenerator() {
        this("SHA-256");
    }

    /**
     * Constructs a new NormalizedHashGenerator instance using the provided
     * message digest algorithm.
     *
     * @param algorithm the name of the message digest algorithm to use.
     *        For example, "SHA-256". If the specified algorithm is not
     *        available, the message digest will not be initialized, and
     *        subsequent attempts to use this instance will not give results.
     */
    public NormalizedHashGenerator(String string) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(string);
        } catch (NoSuchAlgorithmException e) {
            messageDigest = null;
        }
        this.messageDigest = messageDigest;
    }

    /**
     * Applies this function to the given input string.
     *
     * @param message the input String to be hashed.
     * @return an {@link Optional} containing the lowercase hexadecimal
     *         SHA-256 hash of the input string, or an empty {@link Optional}
     *         if the input is null or there's an error during hashing.
     */
    @Override
    public Optional<String> apply(String message) {
        if (message == null || messageDigest == null) {
            return Optional.empty();
        }
        var preparedMessage = message.trim()
            .toLowerCase(Locale.getDefault())
            .getBytes();

        messageDigest.update(preparedMessage);
        byte[] digest = messageDigest.digest();
        return Optional.of(convertBytesToHex(digest));
    }


    private static String convertBytesToHex(byte[] data) {
        var buffer = ByteBuffer.wrap(data);
        return Stream.generate(buffer::get).
                        limit(buffer.capacity()).
                        map(NormalizedHashGenerator::convertByteToHex).
                        collect(Collectors.joining());
    }

    private static String convertByteToHex(Byte data) {
        return String.format("%02x", 0xFF & data);
    }
}

