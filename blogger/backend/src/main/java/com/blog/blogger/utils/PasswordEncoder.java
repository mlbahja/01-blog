package com.blog.blogger.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncoder {
    private final SecureRandom random = new SecureRandom();

    public String encode(String password) {
        try {
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        String hashedAttempt = encode(rawPassword);
        return MessageDigest.isEqual(
            hashedAttempt.getBytes(),
            encodedPassword.getBytes()
        );
    }
}