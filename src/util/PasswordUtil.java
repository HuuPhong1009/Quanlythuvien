package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String hashPassword(String password) {
        if (password == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("PasswordUtil hash error: " + e.getMessage());
            return password;
        }
    }

    public static boolean checkPassword(String plainPassword, String storedPasswordOrHash) {
        if (plainPassword == null || storedPasswordOrHash == null) {
            return false;
        }
        // Match either SHA-256 hash or plain text for initial DB compatibility
        if (plainPassword.equals(storedPasswordOrHash)) {
            return true;
        }
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equalsIgnoreCase(storedPasswordOrHash);
    }
}
