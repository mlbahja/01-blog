package com.blog.blogger.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.CommentLike;
import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.CommentLikeRepository;
import com.blog.blogger.repository.CommentRepository;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
    }

     // ADD THIS METHOD: Get comment by ID
    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Page<Comment> getCommentsByPost(Post post, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return commentRepository.findByPost(post, pageable);
    }

    // ADD THIS METHOD: Delete a comment
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        // Remove all likes associated with this comment first
        commentLikeRepository.deleteByComment(comment);
        
        // Then delete the comment
        commentRepository.delete(comment);
    }
    
    @Transactional
    public Comment likeComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (commentLikeRepository.existsByUserAndComment(user, comment)) {
            return comment; 
        }

        CommentLike like = CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
        commentLikeRepository.save(like);

        comment.setLikeCount(comment.getLikeCount() + 1);
        return commentRepository.save(comment);
    }

    
    @Transactional
    public Comment unlikeComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<CommentLike> likeOpt = commentLikeRepository.findByUserAndComment(user, comment);
        if (likeOpt.isEmpty()) {
            return comment; 
        }

        commentLikeRepository.delete(likeOpt.get());

        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        return commentRepository.save(comment);
    }

    
    public boolean hasUserLikedComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentLikeRepository.existsByUserAndComment(user, comment);
    }
}
