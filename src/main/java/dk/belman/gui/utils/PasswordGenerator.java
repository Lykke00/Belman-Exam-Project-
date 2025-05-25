package dk.belman.gui.utils;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    // f.eks. String password = generatePassword(6, 3); // fx: "a1b2c3-x8y7z9-w3q2er"
    public static String generatePassword(int segmentLength, int segmentCount) {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < segmentCount; i++) {
            if (i > 0)
                password.append("-");

            for (int j = 0; j < segmentLength; j++) {
                int index = random.nextInt(ALPHANUM.length());
                password.append(ALPHANUM.charAt(index));
            }
        }

        return password.toString();
    }
}