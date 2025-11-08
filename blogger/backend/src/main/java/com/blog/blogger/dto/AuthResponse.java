package com.blog.blogger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String accessToken;   // JWT access
    private String refreshToken;  // refresh token (or null if not used)
}
