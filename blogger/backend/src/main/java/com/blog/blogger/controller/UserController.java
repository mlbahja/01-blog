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
import com.blog.blogger.service.SubscriptionService;
import com.blog.blogger.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public UserController(UserService userService, SubscriptionService subscriptionService, UserRepository userRepository) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
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

    /**
     * GET /auth/users
     * Get all users (excluding current user)
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(Principal principal) {
        try {
            String currentUsername = principal.getName();
            List<User> users = userRepository.findAll();

            List<Map<String, Object>> userList = users.stream()
                    .filter(user -> !user.getUsername().equals(currentUsername))
                    .map(user -> {
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("id", user.getId());
                        userMap.put("username", user.getUsername());
                        userMap.put("email", user.getEmail());
                        userMap.put("role", user.getRole());
                        userMap.put("createdAt", user.getCreatedAt());

                        // Add follow stats
                        Map<String, Object> stats = subscriptionService.getFollowStats(user.getUsername());
                        userMap.put("followersCount", stats.get("followersCount"));
                        userMap.put("followingCount", stats.get("followingCount"));

                        // Check if current user is following this user
                        userMap.put("isFollowing", subscriptionService.isFollowing(currentUsername, user.getId()));

                        return userMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /auth/users/{userId}/follow
     * Follow a user
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(@PathVariable Long userId, Principal principal) {
        try {
            subscriptionService.followUser(principal.getName(), userId);
            return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /auth/users/{userId}/follow
     * Unfollow a user
     */
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<?> unfollowUser(@PathVariable Long userId, Principal principal) {
        try {
            subscriptionService.unfollowUser(principal.getName(), userId);
            return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /auth/users/{userId}/is-following
     * Check if current user is following another user
     */
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, Principal principal) {
        boolean isFollowing = subscriptionService.isFollowing(principal.getName(), userId);
        return ResponseEntity.ok(isFollowing);
    }

    /**
     * GET /auth/users/following
     * Get list of users that current user follows
     */
    @GetMapping("/following")
    public ResponseEntity<List<Map<String, Object>>> getFollowing(Principal principal) {
        List<Map<String, Object>> following = subscriptionService.getFollowing(principal.getName());
        return ResponseEntity.ok(following);
    }

    /**
     * GET /auth/users/followers
     * Get list of current user's followers
     */
    @GetMapping("/followers")
    public ResponseEntity<List<Map<String, Object>>> getFollowers(Principal principal) {
        List<Map<String, Object>> followers = subscriptionService.getFollowers(principal.getName());
        return ResponseEntity.ok(followers);
    }
}