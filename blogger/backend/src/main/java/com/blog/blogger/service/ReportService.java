package com.blog.blogger.service;

import com.blog.blogger.dto.CreateReportDTO;
import com.blog.blogger.dto.ReportResponseDTO;
import com.blog.blogger.dto.UpdateReportDTO;
import com.blog.blogger.models.*;
import com.blog.blogger.repository.ReportRepository;
import com.blog.blogger.repository.PostRepository;
import com.blog.blogger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    
    @Autowired
    private ReportRepository reportRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a simple report
     */
    @Transactional
    public ReportResponseDTO createReport(CreateReportDTO dto, User reporter) {
        // Find the post
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Optional: Prevent duplicate reports from same user
        // if (reportRepository.existsByReporterAndPost(reporter, post)) {
        //     throw new RuntimeException("You already reported this post");
        // }
        
        // Create report
        Report report = new Report(reporter, post, dto.getMessage());
        report = reportRepository.save(report);
        
        return new ReportResponseDTO(report);
    }
    
    /**
     * Get all reports (admin)
     */
    public List<ReportResponseDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unresolved reports (admin dashboard)
     */
    public List<ReportResponseDTO> getUnresolvedReports() {
        return reportRepository.findByResolvedFalse().stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's own reports
     */
    public List<ReportResponseDTO> getUserReports(User user) {
        return reportRepository.findByReporter(user).stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Update report (mark as resolved)
     */
    @Transactional
    public ReportResponseDTO updateReport(Long reportId, UpdateReportDTO dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setResolved(dto.isResolved());
        report.setAdminNotes(dto.getAdminNotes());
        
        report = reportRepository.save(report);
        return new ReportResponseDTO(report);
    }
    
    /**
     * Delete report
     */
    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
    
    /**
     * Count unresolved (for admin badge)
     */
    public long countUnresolvedReports() {
        return reportRepository.countByResolvedFalse();
    }
}