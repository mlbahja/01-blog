package com.blog.blogger.controller;

import com.blog.blogger.dto.CreateReportDTO;
import com.blog.blogger.dto.ReportResponseDTO;
import com.blog.blogger.dto.UpdateReportDTO;
import com.blog.blogger.models.User;
import com.blog.blogger.repository.UserRepository;
import com.blog.blogger.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * POST /auth/reports
     * Report a post
     */
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody CreateReportDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User reporter = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            ReportResponseDTO report = reportService.createReport(dto, reporter);
            return ResponseEntity.status(HttpStatus.CREATED).body(report);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create report");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /auth/reports/my
     * Get my reports
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyReports(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<ReportResponseDTO> reports = reportService.getUserReports(user);
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * ========== ADMIN ENDPOINTS ==========
     */
    
    /**
     * GET /auth/reports/admin/all
     * Get all reports (admin)
     */
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllReports() {
        try {
            List<ReportResponseDTO> reports = reportService.getAllReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /auth/reports/admin/unresolved
     * Get unresolved reports (admin dashboard)
     */
    @GetMapping("/admin/unresolved")
    public ResponseEntity<?> getUnresolvedReports() {
        try {
            List<ReportResponseDTO> reports = reportService.getUnresolvedReports();
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get reports");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * GET /auth/reports/admin/count-unresolved
     * Count unresolved (for admin badge)
     */
    @GetMapping("/admin/count-unresolved")
    public ResponseEntity<?> countUnresolvedReports() {
        try {
            long count = reportService.countUnresolvedReports();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to count reports");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * PUT /auth/reports/admin/{id}
     * Update report (mark as resolved)
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody UpdateReportDTO dto) {
        try {
            ReportResponseDTO report = reportService.updateReport(id, dto);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update report");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * DELETE /auth/reports/admin/{id}
     * Delete report
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(Map.of("message", "Report deleted"));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete report");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}