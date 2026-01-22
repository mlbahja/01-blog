package com.blog.blogger.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.blog.blogger.dto.AdminStatsDTO;
import com.blog.blogger.dto.UserProfileDTO;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.Role;
import com.blog.blogger.repository.PostRepository;
import com.blog.blogger.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final PostService postService;

    public AdminService(UserRepository userRepository, PostRepository postRepository, UserService userService,
            PostService postService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.postService = postService;
    }

    public AdminStatsDTO getDashboardStats() {
        List<com.blog.blogger.models.User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.size();
        long bannedUsers = allUsers.stream().filter(com.blog.blogger.models.User::getIsBanned).count();
        long adminUsers = allUsers.stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .count();

        long activeUsers = allUsers.stream()
                .filter(user -> !user.getIsBanned())
                .count();

        long totalPosts = postRepository.count();

        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        long postsToday = postRepository.findByOrderByCreatedAtDesc().stream()
                .filter(post -> post.getCreatedAt().isAfter(startOfToday))
                .count();

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        long newUsersThisWeek = allUsers.stream()
                .filter(user -> user.getCreatedAt().isAfter(oneWeekAgo))
                .count();

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .totalPosts(totalPosts)
                .totalComments(0L)
                .activeUsers(activeUsers)
                .bannedUsers(bannedUsers)
                .adminUsers(adminUsers)
                .postsToday(postsToday)
                .commentsToday(0L)
                .newUsersThisWeek(newUsersThisWeek)
                .build();
    }

    public List<UserProfileDTO> getAllUsers() {
        return userService.getAllUserProfiles();
    }

    public void banUser(Long userId) {
        userService.banUser(userId);
    }

    public void unbanUser(Long userId) {
        userService.unbanUser(userId);
    }

    public void changeUserRole(Long userId, Role newRole) {
        userService.changeUserRole(userId, newRole);
    }

    public void deleteUser(Long userId) {
        userService.deleteUser(userId);
    }

    public List<com.blog.blogger.models.Post> getAllPosts() {
        return postRepository.findAll();
    }

    public void deletePost(Long postId) {
        postService.deletePost(postId);
    }

    public void hidePost(Long postId) {
        postService.hidePost(postId);
    }

    public void unhidePost(Long postId) {
        postService.unhidePost(postId);
    }

    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable);
    }

}
