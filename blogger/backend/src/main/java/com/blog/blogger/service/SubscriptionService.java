package com.blog.blogger.service;

import com.blog.blogger.models.Subscription;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.SubscriptionRepository;
import com.blog.blogger.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Follow a user
     */
    @Transactional
    public Subscription followUser(String currentUsername, Long userIdToFollow) {
        User follower = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User following = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        // Check if user is trying to follow themselves
        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("Cannot follow yourself");
        }

        // Check if already following
        if (subscriptionRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new RuntimeException("Already following this user");
        }

        Subscription subscription = Subscription.builder()
                .follower(follower)
                .following(following)
                .build();

        return subscriptionRepository.save(subscription);
    }

    /**
     * Unfollow a user
     */
    @Transactional
    public void unfollowUser(String currentUsername, Long userIdToUnfollow) {
        User follower = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User following = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        subscriptionRepository.deleteByFollowerAndFollowing(follower, following);
    }

    /**
     * Check if current user is following another user
     */
    public boolean isFollowing(String currentUsername, Long userId) {
        User follower = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User following = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.existsByFollowerAndFollowing(follower, following);
    }

    /**
     * Get all users that current user follows
     */
    public List<Map<String, Object>> getFollowing(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions = subscriptionRepository.findByFollower(user);

        return subscriptions.stream()
                .map(sub -> {
                    User followingUser = sub.getFollowing();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", followingUser.getId());
                    userMap.put("username", followingUser.getUsername());
                    userMap.put("email", followingUser.getEmail());
                    userMap.put("followedAt", sub.getCreatedAt());
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get all followers of current user
     */
    public List<Map<String, Object>> getFollowers(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions = subscriptionRepository.findByFollowing(user);

        return subscriptions.stream()
                .map(sub -> {
                    User followerUser = sub.getFollower();
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", followerUser.getId());
                    userMap.put("username", followerUser.getUsername());
                    userMap.put("email", followerUser.getEmail());
                    userMap.put("followedAt", sub.getCreatedAt());
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get follow statistics for a user
     */
    public Map<String, Object> getFollowStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("followersCount", subscriptionRepository.countByFollowing(user));
        stats.put("followingCount", subscriptionRepository.countByFollower(user));

        return stats;
    }

    /**
     * Get IDs of all users that current user follows (for filtering posts)
     */
    public List<Long> getFollowingIds(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Subscription> subscriptions = subscriptionRepository.findByFollower(user);

        return subscriptions.stream()
                .map(sub -> sub.getFollowing().getId())
                .collect(Collectors.toList());
    }
}
