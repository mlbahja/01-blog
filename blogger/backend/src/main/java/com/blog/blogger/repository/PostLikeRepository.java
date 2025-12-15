package com.blog.blogger.repository;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.PostLike;
import com.blog.blogger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * PostLikeRepository - Data access layer for PostLike entity
 *
 * Provides methods to:
 * - Check if a user has liked a post
 * - Find a specific like record
 * - Count likes for a post
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    /**
     * Find a like record for a specific user and post combination
     */
    Optional<PostLike> findByUserAndPost(User user, Post post);
    /**
     * Check if a user has liked a specific post
     */
    boolean existsByUserAndPost(User user, Post post);
    /**
     * Count the number of likes for a specific post
     */
    long countByPost(Post post);
    /**
     * Delete a like by user and post
     */
    void deleteByUserAndPost(User user, Post post);
}
