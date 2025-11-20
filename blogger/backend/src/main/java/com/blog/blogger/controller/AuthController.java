package com.blog.blogger.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.blogger.dto.AuthResponse;
import com.blog.blogger.dto.LoginRequest;
import com.blog.blogger.dto.RegisterRequest;
import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.security.JwtUtil;
import com.blog.blogger.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userService.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        if (userService.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already in use");
        }

        log.info("New user registration: {}", req.getUsername());

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword())
                .role(Role.USER)
                .build();

        User saved = userService.register(user);

        // Generate JWT token for the newly registered user (auto-login)
        String token = jwtUtil.generateToken(saved.getUsername());

        // Create response with user info and JWT token
        AuthResponse response = new AuthResponse(
            saved.getId(),
            saved.getUsername(),
            saved.getEmail(),
            saved.getRole(),  // Include user role
            token,
            null  // refreshToken - not implemented yet
        );

        log.info("User '{}' registered successfully and logged in", saved.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        String identifier = (req.getEmail() != null && !req.getEmail().isBlank())
                ? req.getEmail()
                : req.getUsername();

        log.info("Login attempt for: {}", identifier);
        var opt = userService.login(identifier, req.getPassword());

        if (opt.isEmpty()) {
            log.warn("Login failed for user: {}", identifier);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = opt.get();

        // Generate JWT token for the authenticated user
        String token = jwtUtil.generateToken(user.getUsername());

        // Create response with user info and JWT token
        AuthResponse response = new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),  // Include user role
            token,
            null  // refreshToken - not implemented yet
        );

        log.info("User '{}' logged in successfully", user.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/home")
    public String HomeHanler() {
        return "test this is home";
    }
}
