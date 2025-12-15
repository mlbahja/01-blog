package com.blog.blogger.repository;

import com.blog.blogger.models.Report;
import com.blog.blogger.models.User;
import com.blog.blogger.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Check if user already reported this post (optional feature)
    boolean existsByReporterAndPost(User reporter, Post post);
    
    // Get unresolved reports (for admin panel)
    List<Report> findByResolvedFalse();
    
    // Get user's own reports
    List<Report> findByReporter(User reporter);
    
    // Get reports for a specific post
    List<Report> findByPost(Post post);
    
    // Count unresolved reports (for admin badge)
    long countByResolvedFalse();
}