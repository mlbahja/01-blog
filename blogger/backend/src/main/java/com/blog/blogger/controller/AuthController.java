package com.blog.blogger.controller;

import com.blog.blogger.dto.RegisterRequest;
import com.blog.blogger.dto.LoginRequest;
import com.blog.blogger.models.User;
import com.blog.blogger.models.Role;
import com.blog.blogger.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userService.existsByEmail(req.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use");
        }
        if (userService.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already in use");
        }

        // map DTO -> entity (explicit)
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword()) // service will encode
                .role(Role.USER) // NEVER from client
                .build();

        User saved = userService.register(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        // Accept either email or username from the request. If frontend sends username, use it;
        // otherwise use email. The service will try both if needed.
        String identifier = (req.getEmail() != null && !req.getEmail().isBlank()) ? req.getEmail() : req.getUsername();
        var opt = userService.login(identifier, req.getPassword());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        User user = opt.get();
        // Remove sensitive information before sending
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/home")
    public String HomeHanler(){
        return "test this is home";
    }
}
