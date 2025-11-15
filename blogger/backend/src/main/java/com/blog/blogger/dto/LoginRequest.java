package com.blog.blogger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String username;
}
