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
    
    
    @Transactional
    public ReportResponseDTO createReport(CreateReportDTO dto, User reporter) {
       
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
      
        
        
        Report report = new Report(reporter, post, dto.getMessage());
        report = reportRepository.save(report);
        
        return new ReportResponseDTO(report);
    }
    
    
    public List<ReportResponseDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
   
    public List<ReportResponseDTO> getUnresolvedReports() {
        return reportRepository.findByResolvedFalse().stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    
    public List<ReportResponseDTO> getUserReports(User user) {
        return reportRepository.findByReporter(user).stream()
                .map(ReportResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    
    @Transactional
    public ReportResponseDTO updateReport(Long reportId, UpdateReportDTO dto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        report.setResolved(dto.isResolved());
        report.setAdminNotes(dto.getAdminNotes());
        
        report = reportRepository.save(report);
        return new ReportResponseDTO(report);
    }
    
    
    @Transactional
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
    
    
    public long countUnresolvedReports() {
        return reportRepository.countByResolvedFalse();
    }
}