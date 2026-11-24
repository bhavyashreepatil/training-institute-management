import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Batch, BatchRoster } from '../../../core/models/batch.model';
import { Enrolment } from '../../../core/models/enrolment.model';
import { BatchService } from '../../../core/services/batch.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-batch-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent,
    StatusBadgeComponent
  ],
  templateUrl: './batch-detail.component.html',
  styleUrl: './batch-detail.component.scss'
})
export class BatchDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly batchService = inject(BatchService);

  roster: BatchRoster | null = null;
  loading = true;
  errorMessage = '';

  get batch(): Batch | null {
    return this.roster?.batch ?? null;
  }

  get enrolments(): Enrolment[] {
    return this.roster?.enrolments ?? [];
  }

  get seatUtilizationPercent(): number {
    if (!this.batch || this.batch.maxCapacity <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((this.batch.enrolledCount / this.batch.maxCapacity) * 100));
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.batchService.getRoster(id).subscribe({
      next: (data) => {
        this.roster = data;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }
}
