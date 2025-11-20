package com.blog.blogger.dto;

import com.blog.blogger.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;            // User role (USER or ADMIN)
    private String accessToken;   // JWT access token
    private String refreshToken;  // refresh token (or null if not used)
}
