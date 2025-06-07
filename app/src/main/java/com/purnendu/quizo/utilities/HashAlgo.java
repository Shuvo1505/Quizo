package com.purnendu.quizo.utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A utility class for hashing passwords using the SHA-256 algorithm.
 * This class provides a static method to securely hash a given password string,
 * which is essential for storing passwords in a database without storing them in plaintext.
 *
 * @author Purnendu Guha
 * @version 2.0.1
 */
//Class for Hashing
public class HashAlgo {
    /**
     * Hashes the given password string using the SHA-256 algorithm.
     * The input password string is converted to bytes using UTF-8 encoding,
     * then digested, and finally converted into a hexadecimal string representation.
     *
     * @param password The plaintext password string to be hashed.
     * @return The SHA-256 hashed password as a hexadecimal string.
     * @throws RuntimeException if the SHA-256 algorithm is not available (a {@link java.security.NoSuchAlgorithmException}).
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This exception should ideally not occur as SHA-256 is a standard algorithm.
            throw new RuntimeException("SHA-256 algorithm not found", e);
        } catch (Exception e) {
            // Catch any other unexpected exceptions during the hashing process.
            throw new RuntimeException("Error during password hashing", e);
        }
    }
}
