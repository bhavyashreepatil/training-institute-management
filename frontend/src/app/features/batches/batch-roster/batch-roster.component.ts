import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Batch, BatchRoster } from '../../../core/models/batch.model';
import { AttendanceUpdateRequest, Enrolment } from '../../../core/models/enrolment.model';
import { BatchService } from '../../../core/services/batch.service';
import { EnrolmentService } from '../../../core/services/enrolment.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-batch-roster',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent,
    StatusBadgeComponent
  ],
  templateUrl: './batch-roster.component.html',
  styleUrl: './batch-roster.component.scss'
})
export class BatchRosterComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly batchService = inject(BatchService);
  private readonly enrolmentService = inject(EnrolmentService);

  batchId = 0;
  roster: BatchRoster | null = null;
  attendanceDrafts: Record<number, number> = {};
  remarksDrafts: Record<number, string> = {};
  savingIds: Record<number, boolean> = {};
  loading = true;
  errorMessage = '';
  successMessage = '';

  get batch(): Batch | null {
    return this.roster?.batch ?? null;
  }

  get enrolments(): Enrolment[] {
    return this.roster?.enrolments ?? [];
  }

  ngOnInit(): void {
    this.batchId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadRoster();
  }

  loadRoster(): void {
    this.loading = true;
    this.errorMessage = '';
    this.batchService.getRoster(this.batchId).subscribe({
      next: (data) => {
        this.roster = data;
        this.initDrafts(data.enrolments);
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }

  private initDrafts(enrolments: Enrolment[]): void {
    this.attendanceDrafts = {};
    this.remarksDrafts = {};
    for (const enrolment of enrolments) {
      this.attendanceDrafts[enrolment.id] = enrolment.attendancePercentage;
      this.remarksDrafts[enrolment.id] = enrolment.remarks ?? '';
    }
  }

  isInvalidAttendance(enrolmentId: number): boolean {
    const value = Number(this.attendanceDrafts[enrolmentId]);
    return Number.isNaN(value) || value < 0 || value > 100;
  }

  saveAttendance(enrolment: Enrolment): void {
    const percentage = Number(this.attendanceDrafts[enrolment.id]);
    if (Number.isNaN(percentage) || percentage < 0 || percentage > 100) {
      this.errorMessage = `Attendance percentage for ${enrolment.studentName} must be between 0 and 100.`;
      this.successMessage = '';
      return;
    }

    this.savingIds[enrolment.id] = true;
    this.errorMessage = '';
    this.successMessage = '';

    const remarksText = (this.remarksDrafts[enrolment.id] ?? '').trim();
    const payload: AttendanceUpdateRequest = {
      attendancePercentage: Number(percentage.toFixed(2)),
      remarks: remarksText.length > 0 ? remarksText : null
    };

    this.enrolmentService.updateAttendance(enrolment.id, payload).subscribe({
      next: (updated) => {
        this.savingIds[enrolment.id] = false;
        if (this.roster) {
          this.roster = {
            ...this.roster,
            enrolments: this.roster.enrolments.map((item) =>
              item.id === updated.id ? updated : item
            )
          };
          this.recalculateAverageAttendance();
        }
        this.attendanceDrafts[updated.id] = updated.attendancePercentage;
        this.remarksDrafts[updated.id] = updated.remarks ?? '';
        this.successMessage = `Updated attendance for ${updated.studentName} to ${updated.attendancePercentage}%.`;
      },
      error: (err: Error) => {
        this.savingIds[enrolment.id] = false;
        this.errorMessage = err.message;
      }
    });
  }

  private recalculateAverageAttendance(): void {
    if (!this.roster || this.roster.enrolments.length === 0) {
      return;
    }
    const total = this.roster.enrolments.reduce(
      (sum, item) => sum + item.attendancePercentage,
      0
    );
    this.roster = {
      ...this.roster,
      averageAttendancePercentage: total / this.roster.enrolments.length
    };
  }
}
