import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { ReportService } from '../../core/services/report.service';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { Report } from '../../core/models/report.model';

@Component({
  selector: 'app-admin-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class AdminReportsComponent implements OnInit {
  reports: Report[] = [];
  showResolvedOnly = false;
  unresolvedCount = 0;
  loading = false;
  username: string = '';

  constructor(
    private reportService: ReportService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userData = this.authService.getUserData();
    this.username = userData?.username || 'Admin';
    this.loadReports();
    this.loadUnresolvedCount();
  }

  loadReports(): void {
    this.loading = true;
    const observable = this.showResolvedOnly
      ? this.reportService.getAllReports()
      : this.reportService.getUnresolvedReports();

    observable.subscribe({
      next: (data) => {
        this.reports = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading reports:', err);
        this.toastService.show('Failed to load reports', 'error');
        this.loading = false;
      }
    });
  }

  loadUnresolvedCount(): void {
    this.reportService.getUnresolvedCount().subscribe({
      next: (data) => {
        this.unresolvedCount = data.count;
      },
      error: (err) => {
        console.error('Error loading count:', err);
      }
    });
  }

  markResolved(report: Report): void {
    const notes = prompt('Add admin notes (optional):');

    this.reportService.updateReport(report.id, {
      resolved: true,
      adminNotes: notes || undefined
    }).subscribe({
      next: () => {
        report.resolved = true;
        report.adminNotes = notes || undefined;
        this.toastService.show('Report marked as resolved', 'success');
        this.loadUnresolvedCount();
      },
      error: (err) => {
        console.error('Error updating report:', err);
        this.toastService.show('Failed to update report', 'error');
      }
    });
  }

  deleteReport(report: Report): void {
    if (!confirm(`Delete report for post "${report.postTitle}"?`)) {
      return;
    }

    this.reportService.deleteReport(report.id).subscribe({
      next: () => {
        this.reports = this.reports.filter(r => r.id !== report.id);
        this.toastService.show('Report deleted successfully', 'success');
        if (!report.resolved) {
          this.loadUnresolvedCount();
        }
      },
      error: (err) => {
        console.error('Error deleting report:', err);
        this.toastService.show('Failed to delete report', 'error');
      }
    });
  }

  toggleFilter(): void {
    this.showResolvedOnly = !this.showResolvedOnly;
    this.loadReports();
  }

  viewPost(postId: number): void {
    window.open(`/post/${postId}`, '_blank');
  }

  logout(): void {
    this.authService.logout(true);
  }

  isAdmin(): boolean {
    const userData = this.authService.getUserData();
    return userData?.role === 'ADMIN';
  }
}
