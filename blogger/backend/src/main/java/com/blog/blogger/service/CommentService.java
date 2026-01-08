package com.blog.blogger.service;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.CommentLike;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.CommentLikeRepository;
import com.blog.blogger.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    public CommentService(CommentRepository commentRepository, CommentLikeRepository commentLikeRepository) {
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
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
