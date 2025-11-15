package com.blog.blogger.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    /**
     * Try to login using either email or username as identifier. If the
     * identifier matches an email or username and the password matches, returns
     * the user.
     */

    public Optional<User> login(String identifier, String password) {
        if (identifier == null) {
            return Optional.empty();
        }

        // Try email first
        Optional<User> existingUser = userRepository.findByEmail(identifier);
        if (existingUser.isEmpty()) {
            // Try username
            existingUser = userRepository.findByUsername(identifier);
        }

        if (existingUser.isPresent() && passwordEncoder.matches(password, existingUser.get().getPassword())) {
            return existingUser;
        }

        return Optional.empty();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
