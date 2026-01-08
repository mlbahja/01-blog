package com.blog.blogger.repository;

import com.blog.blogger.models.Subscription;
import com.blog.blogger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

    List<Subscription> findByFollower(User follower);

    List<Subscription> findByFollowing(User following);

    long countByFollower(User follower);

    long countByFollowing(User following);

    void deleteByFollowerAndFollowing(User follower, User following);
}
