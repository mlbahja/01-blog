package com.blog.blogger.repository;

import com.blog.blogger.models.Report;
import com.blog.blogger.models.User;
import com.blog.blogger.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
   
    boolean existsByReporterAndPost(User reporter, Post post);
    
    
    List<Report> findByResolvedFalse();
    
   
    List<Report> findByReporter(User reporter);
    

    List<Report> findByPost(Post post);
    
    
    long countByResolvedFalse();
}