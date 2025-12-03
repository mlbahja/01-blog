package com.blog.blogger.repository;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.CommentLike;
import com.blog.blogger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CommentLikeRepository - Database operations for comment likes
 */
@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    /**
     * Check if a user has liked a specific comment
     */
    boolean existsByUserAndComment(User user, Comment comment);

    /**
     * Find a like by user and comment
     */
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);

    /**
     * Count likes for a comment
     */
    long countByComment(Comment comment);
}
