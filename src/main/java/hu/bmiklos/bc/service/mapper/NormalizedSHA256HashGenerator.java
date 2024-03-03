package hu.bmiklos.bc.service.mapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public class NormalizedSHA256HashGenerator implements Function<String, Optional<String>> {

    @Override
    public Optional<String> apply(String message) {
        if (message == null) {
            return Optional.empty();
        }
        var preparedMessage = message.trim()
            .toLowerCase(Locale.getDefault())
            .getBytes();

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            return Optional.empty();
        }
        messageDigest.update(preparedMessage);
        byte[] digest = messageDigest.digest();
        return Optional.of(convertByteToHex(digest));
    }

    private static String convertByteToHex(byte[] data) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            hexString.append(Integer.toHexString(0xFF & data[i]));
        }
        return hexString.toString();
    }
}
