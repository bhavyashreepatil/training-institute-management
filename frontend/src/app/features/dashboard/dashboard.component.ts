import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Batch } from '../../core/models/batch.model';
import { DashboardSummary } from '../../core/models/dashboard.model';
import { DashboardService } from '../../core/services/dashboard.service';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent,
    StatusBadgeComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  summary: DashboardSummary | null = null;
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.loading = true;
    this.errorMessage = '';
    this.dashboardService.getSummary().subscribe({
      next: (data) => {
        this.summary = data;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }

  getSeatFillPercentage(batch: Batch): number {
    if (batch.maxCapacity <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((batch.enrolledCount / batch.maxCapacity) * 100));
  }

  getProgressBarClass(batch: Batch): string {
    const pct = this.getSeatFillPercentage(batch);
    if (pct >= 90) {
      return 'bg-danger';
    }
    if (pct >= 70) {
      return 'bg-warning';
    }
    return 'bg-success';
  }
}
