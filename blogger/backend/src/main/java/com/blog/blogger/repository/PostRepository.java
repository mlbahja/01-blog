package com.blog.blogger.repository;

import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor(User author);

    List<Post> findByOrderByCreatedAtDesc();

    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);
}
