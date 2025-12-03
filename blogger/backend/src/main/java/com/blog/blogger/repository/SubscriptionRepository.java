package com.blog.blogger.repository;

import com.blog.blogger.models.Subscription;
import com.blog.blogger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Check if a subscription exists between follower and following
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Find a specific subscription
    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

    // Get all users that this user follows
    List<Subscription> findByFollower(User follower);

    // Get all followers of this user
    List<Subscription> findByFollowing(User following);

    // Count how many users this user follows
    long countByFollower(User follower);

    // Count how many followers this user has
    long countByFollowing(User following);

    // Delete subscription
    void deleteByFollowerAndFollowing(User follower, User following);
}
