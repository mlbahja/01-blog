package  com.blog.blogger.repository;



import com.blog.blogger.models.ReportPost;
import com.blog.blogger.models.User;
import com.blog.blogger.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface PostReportRepository extends  JpaRepository<ReportPost, Long >{

  // Check if user already reported this post (optional feature)
    boolean existsByReporterAndPost(User reporter, Post post);
    
    // Get unresolved reports (for admin panel)
    List<ReportPost> findByResolvedFalse();
    
    // Get user's own reports
    List<ReportPost> findByReporter(User reporter);
    
    // Get reports for a specific post
    List<ReportPost> findByPost(Post post);
    
    // Count unresolved reports (for admin badge)
    long countByResolvedFalse();
}
