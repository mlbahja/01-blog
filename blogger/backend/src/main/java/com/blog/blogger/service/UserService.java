package com.blog.blogger.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.blog.blogger.Repositories.UserRepository;
import com.blog.blogger.entity.User;
public class UserService {
    @Autowired
    private    UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
   public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role
        user.setRole(user.getRole() != null ? user.getRole() : com.blog.blogger.entity.Role.USER);

        return userRepository.save(user);
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
