package com.blog.blogger.repository;

import com.blog.blogger.models.Comment;
import com.blog.blogger.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    Page<Comment> findByPost(Post post, Pageable pageable);
}
