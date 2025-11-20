package com.blog.blogger.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.blogger.dto.ChangePasswordDTO;
import com.blog.blogger.dto.UpdateProfileDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.Role;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;

/**
 * UserService - Handles user-related business logic
 *
 * Features:
 * - User registration and authentication
 * - Profile management (view, update, delete)
 * - Password management
 * - User data retrieval
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== Authentication Methods ==========

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

    // ========== User Retrieval Methods ==========

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<UserProfileDTO> getAllUserProfiles() {
        return userRepository.findAll().stream()
                .map(this::convertToProfileDTO)
                .collect(Collectors.toList());
    }

    // ========== Profile Management Methods ==========

    public UserProfileDTO getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return convertToProfileDTO(user);
    }

    public UserProfileDTO updateProfile(Long id, UpdateProfileDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update only non-null fields
        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }
        if (dto.getBio() != null) {
            user.setBio(dto.getBio());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(dto.getProfilePictureUrl());
        }

        User updatedUser = userRepository.save(user);
        return convertToProfileDTO(updatedUser);
    }

    // ========== Password Management ==========

    public void changePassword(Long id, ChangePasswordDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Verify new passwords match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        // Validate new password
        if (dto.getNewPassword().length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // ========== User Management Methods ==========

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }

    public void banUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsBanned(true);
        user.setBannedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void unbanUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsBanned(false);
        user.setBannedAt(null);
        userRepository.save(user);
    }

    public void changeUserRole(Long id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setRole(newRole);
        userRepository.save(user);
    }

    // ========== Validation Methods ==========

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    // ========== Helper Methods ==========

    public UserProfileDTO convertToProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .profilePictureUrl(user.getProfilePictureUrl())
                .role(user.getRole())
                .isBanned(user.getIsBanned())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
