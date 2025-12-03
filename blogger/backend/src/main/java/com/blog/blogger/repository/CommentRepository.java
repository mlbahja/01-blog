package com.blog.blogger.repository;

import com.blog.blogger.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CommentRepository - Database operations for comments
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
