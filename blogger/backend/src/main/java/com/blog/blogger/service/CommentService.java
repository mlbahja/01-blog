package com.blog.blogger.service;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.CommentLike;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.CommentLikeRepository;
import com.blog.blogger.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * CommentService - Handles comment-related business logic
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

    /**
     * Like a comment
     */
    @Transactional
    public Comment likeComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user already liked this comment
        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            return comment; // Already liked, do nothing
        }

        // Create new like
        CommentLike like = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
        commentLikeRepository.save(like);

        // Increment like count
        comment.setLikeCount(comment.getLikeCount() + 1);
        return commentRepository.save(comment);
    }

    /**
     * Unlike a comment
     */
    @Transactional
    public Comment unlikeComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Check if user has liked this comment
        Optional<CommentLike> likeOpt = commentLikeRepository.findByUserAndComment(user, comment);
        if (likeOpt.isEmpty()) {
            return comment; // Not liked, do nothing
        }

        // Delete the like
        commentLikeRepository.delete(likeOpt.get());

        // Decrement like count (but don't go below 0)
        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        return commentRepository.save(comment);
    }

    /**
     * Check if a user has liked a specific comment
     */
    public boolean hasUserLikedComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentLikeRepository.existsByUserAndComment(user, comment);
    }
}
