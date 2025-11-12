package com.blog.blogger.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoder {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String encode(String password) {
        if (password == null) throw new IllegalArgumentException("password == null");
        return encoder.encode(password);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return encoder.matches(rawPassword, encodedPassword);
    }
}

