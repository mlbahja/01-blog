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
    
    Optional<PostLike> findByUserAndPost(User user, Post post);
    
    boolean existsByUserAndPost(User user, Post post);
    
    long countByPost(Post post);
   
    void deleteByUserAndPost(User user, Post post);

    void deleteByPost(Post post);
}
