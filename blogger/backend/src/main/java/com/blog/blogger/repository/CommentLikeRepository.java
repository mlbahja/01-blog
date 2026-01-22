package com.blog.blogger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.CommentLike;
import com.blog.blogger.models.User;


@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    
    boolean existsByUserAndComment(User user, Comment comment);

    
    Optional<CommentLike> findByUserAndComment(User user, Comment comment);

      void deleteByComment(Comment comment);
    long countByComment(Comment comment);
}
