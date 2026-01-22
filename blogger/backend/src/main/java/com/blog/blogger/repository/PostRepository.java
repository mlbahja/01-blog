package com.blog.blogger.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blog.blogger.models.Post;
import com.blog.blogger.models.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthor(User author);
    
    List<Post> findByOrderByCreatedAtDesc();

    List<Post> findByAuthorIdInOrderByCreatedAtDesc(List<Long> authorIds);

  
    Page<Post> findByIsHiddenFalseOrIsHiddenIsNull(Pageable pageable);

   
    @Query("SELECT p FROM Post p WHERE p.author.id IN :authorIds AND (p.isHidden = false OR p.isHidden IS NULL) ORDER BY p.createdAt DESC")
    List<Post> findNonHiddenPostsByAuthorIds(@Param("authorIds") List<Long> authorIds);
}
