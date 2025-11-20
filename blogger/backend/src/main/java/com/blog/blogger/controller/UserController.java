package com.blog.blogger.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.blog.blogger.dto.ChangePasswordDTO;
import com.blog.blogger.dto.UpdateProfileDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.User;
import com.blog.blogger.service.UserService;

/**
 * UserController - Handles user profile operations
 *
 * Endpoints:
 * - GET /auth/users/me - Get current user profile
 * - GET /auth/users/{id} - Get user profile by ID
 * - PUT /auth/users/{id} - Update user profile
 * - PUT /auth/users/{id}/password - Change password
 * - DELETE /auth/users/{id} - Delete user account
 */
@RestController
@RequestMapping("/auth/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /auth/users/me
     * Get current logged-in user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile(Principal principal) {
        try {
            String username = principal.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserProfileDTO profile = userService.convertToProfileDTO(user);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /auth/users/{id}
     * Get user profile by ID (public - anyone can view)
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileDTO profile = userService.getUserProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/users/{id}
     * Update user profile (owner only or admin)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody UpdateProfileDTO updateDTO,
            Principal principal) {
        try {
            // Get current user
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is updating their own profile or is admin
            if (!currentUser.getId().equals(id) && !userService.isAdmin(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only update your own profile"));
            }

            UserProfileDTO updatedProfile = userService.updateProfile(id, updateDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/users/{id}/password
     * Change user password (owner only)
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal principal) {
        try {
            // Get current user
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Users can only change their own password (not even admin can change others' passwords)
            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only change your own password"));
            }

            userService.changePassword(id, changePasswordDTO);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /auth/users/{id}
     * Delete user account (owner only or admin)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Principal principal) {
        try {
            // Get current user
            String username = principal.getName();
            User currentUser = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is deleting their own account or is admin
            if (!currentUser.getId().equals(id) && !userService.isAdmin(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only delete your own account"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}