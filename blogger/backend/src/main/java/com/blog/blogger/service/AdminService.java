package com.blog.blogger.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.blog.blogger.dto.AdminStatsDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.Role;
import com.blog.blogger.repository.PostRepository;
import com.blog.blogger.repository.UserRepository;

/**
 * AdminService - Handles admin-related business logic
 *
 * Features:
 * - Dashboard statistics
 * - User management (view all, ban/unban, role changes, delete)
 * - Post moderation
 * - System-wide operations
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public AdminService(UserRepository userRepository, PostRepository postRepository, UserService userService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    // ========== Dashboard Statistics ==========

    /**
     * Get comprehensive statistics for admin dashboard
     */
    public AdminStatsDTO getDashboardStats() {
        List<com.blog.blogger.models.User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long bannedUsers = allUsers.stream().filter(com.blog.blogger.models.User::getIsBanned).count();
        long adminUsers = allUsers.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();

        // Active users (logged in within last 30 days) - for now just count non-banned
        long activeUsers = allUsers.stream()
                .filter(user -> !user.getIsBanned())
                .count();

        // Post statistics
        long totalPosts = postRepository.count();

        // Posts today
        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        long postsToday = postRepository.findByOrderByCreatedAtDesc().stream()
                .filter(post -> post.getCreatedAt().isAfter(startOfToday))
                .count();

        // New users this week
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        long newUsersThisWeek = allUsers.stream()
                .filter(user -> user.getCreatedAt().isAfter(oneWeekAgo))
                .count();

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalComments(0L) // TODO: Implement when CommentRepository is available
                .activeUsers(activeUsers)
                .bannedUsers(bannedUsers)
                .adminUsers(adminUsers)
                .postsToday(postsToday)
                .commentsToday(0L) // TODO: Implement when CommentRepository is available
                .newUsersThisWeek(newUsersThisWeek)
                .build();
    }

    // ========== User Management ==========

    /**
     * Get all users (admin only)
     */
    public List<UserProfileDTO> getAllUsers() {
        return userService.getAllUserProfiles();
    }

    /**
     * Ban a user
     */
    public void banUser(Long userId) {
        userService.banUser(userId);
    }

    /**
     * Unban a user
     */
    public void unbanUser(Long userId) {
        userService.unbanUser(userId);
    }

    /**
     * Change user role (promote/demote)
     */
    public void changeUserRole(Long userId, Role newRole) {
        userService.changeUserRole(userId, newRole);
    }

    /**
     * Delete a user (admin only)
     */
    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }

    // ========== Post Moderation ==========

    /**
     * Get all posts (for moderation)
     */
    public List<com.blog.blogger.models.Post> getAllPosts() {
        return postRepository.findAll();
    }

    /**
     * Delete a post (admin only)
     */
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
