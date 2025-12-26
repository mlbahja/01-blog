package com.blog.blogger.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.blog.blogger.dto.AdminStatsDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.Role;
import com.blog.blogger.service.AdminService;

/**
 * AdminController - Handles admin-only operations
 *
 * All endpoints in this controller require ADMIN role
 *
 * Endpoints:
 * - GET /auth/admin/stats - Dashboard statistics
 * - GET /auth/admin/users - Get all users
 * - PUT /auth/admin/users/{id}/ban - Ban a user
 * - PUT /auth/admin/users/{id}/unban - Unban a user
 * - PUT /auth/admin/users/{id}/role - Change user role
 * - DELETE /auth/admin/users/{id} - Delete a user
 * - GET /auth/admin/posts - Get all posts (moderation)
 * - DELETE /auth/admin/posts/{id} - Delete a post
 */
@RestController
@RequestMapping("/auth/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * GET /auth/admin/stats
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            AdminStatsDTO stats = adminService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /auth/admin/users
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserProfileDTO> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/admin/users/{id}/ban
     * Ban a user
     */
    @PutMapping("/users/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long id) {
        try {
            adminService.banUser(id);
            return ResponseEntity.ok(Map.of("message", "User banned successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/admin/users/{id}/unban
     * Unban a user
     */
    @PutMapping("/users/{id}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long id) {
        try {
            adminService.unbanUser(id);
            return ResponseEntity.ok(Map.of("message", "User unbanned successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/admin/users/{id}/role
     * Change user role (promote/demote)
     * Body: { "role": "ADMIN" } or { "role": "USER" }
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String roleStr = body.get("role");
            if (roleStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Role is required"));
            }

            Role newRole;
            try {
                newRole = Role.valueOf(roleStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Invalid role. Must be USER or ADMIN"));
            }

            adminService.changeUserRole(id, newRole);
            return ResponseEntity.ok(Map.of("message", "User role changed to " + newRole));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /auth/admin/users/{id}
     * Delete a user (admin only)
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
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
     * GET /auth/admin/posts
     * Get all posts (for moderation)
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        try {
            List<Post> posts = adminService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/admin/posts/{id}/hide
     * Hide a post (soft delete - post remains in database but hidden from users)
     */
    @PutMapping("/posts/{id}/hide")
    public ResponseEntity<?> hidePost(@PathVariable Long id) {
        try {
            adminService.hidePost(id);
            return ResponseEntity.ok(Map.of("message", "Post hidden successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /auth/admin/posts/{id}/unhide
     * Unhide a previously hidden post
     */
    @PutMapping("/posts/{id}/unhide")
    public ResponseEntity<?> unhidePost(@PathVariable Long id) {
        try {
            adminService.unhidePost(id);
            return ResponseEntity.ok(Map.of("message", "Post unhidden successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /auth/admin/posts/{id}
     * Delete a post permanently (moderation)
     */
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            adminService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
