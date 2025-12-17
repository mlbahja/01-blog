import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Report, CreateReportDTO, UpdateReportDTO } from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = 'http://localhost:8080/auth/reports';

  constructor(private http: HttpClient) {}

  /**
   * User: Create a report for a post
   */
  createReport(reportData: CreateReportDTO): Observable<Report> {
    return this.http.post<Report>(this.apiUrl, reportData);
  }

  /**
   * User: Get my reports
   */
  getMyReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.apiUrl}/my`);
  }

  /**
   * Admin: Get all reports
   */
  getAllReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.apiUrl}/admin/all`);
  }

  /**
   * Admin: Get unresolved reports
   */
  getUnresolvedReports(): Observable<Report[]> {
    return this.http.get<Report[]>(`${this.apiUrl}/admin/unresolved`);
  }

  /**
   * Admin: Get count of unresolved reports
   */
  getUnresolvedCount(): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/admin/count-unresolved`);
  }

  /**
   * Admin: Update report (mark as resolved, add notes)
   */
  updateReport(id: number, updateData: UpdateReportDTO): Observable<Report> {
    return this.http.put<Report>(`${this.apiUrl}/admin/${id}`, updateData);
  }

  /**
   * Admin: Delete report
   */
  deleteReport(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/admin/${id}`);
  }
}
