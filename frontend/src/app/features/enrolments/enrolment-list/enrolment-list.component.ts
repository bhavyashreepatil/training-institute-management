import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Enrolment, EnrolmentRequest, EnrolmentStatus } from '../../../core/models/enrolment.model';
import { EnrolmentService } from '../../../core/services/enrolment.service';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

interface PendingAction {
  type: 'delete' | 'drop';
  enrolment: Enrolment;
}

@Component({
  selector: 'app-enrolment-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    SearchBarComponent,
    DataTableComponent,
    StatusBadgeComponent,
    ConfirmationDialogComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent
  ],
  templateUrl: './enrolment-list.component.html',
  styleUrl: './enrolment-list.component.scss'
})
export class EnrolmentListComponent implements OnInit {
  private readonly enrolmentService = inject(EnrolmentService);

  readonly columns: readonly DataTableColumn[] = [
    { key: 'studentName', label: 'Student' },
    { key: 'batchName', label: 'Batch' },
    { key: 'courseTitle', label: 'Course' },
    { key: 'enrolmentDate', label: 'Enrolled On' },
    { key: 'attendancePercentage', label: 'Attendance' },
    { key: 'status', label: 'Status' },
    { key: 'actions', label: 'Actions', headerClass: 'text-end' }
  ];

  enrolments: Enrolment[] = [];
  filteredEnrolments: Enrolment[] = [];
  searchTerm = '';
  statusFilter: 'ALL' | EnrolmentStatus = 'ALL';
  loading = true;
  errorMessage = '';
  successMessage = '';
  pendingAction: PendingAction | null = null;
  processing = false;

  get dialogTitle(): string {
    if (!this.pendingAction) {
      return '';
    }
    return this.pendingAction.type === 'drop' ? 'Drop Enrolment' : 'Delete Enrolment';
  }

  get dialogMessage(): string {
    if (!this.pendingAction) {
      return '';
    }
    const { type, enrolment } = this.pendingAction;
    if (type === 'drop') {
      return `Mark ${enrolment.studentName} (${enrolment.studentCode}) as DROPPED from ${enrolment.batchCode}?`;
    }
    return `Delete enrolment for ${enrolment.studentName} (${enrolment.studentCode}) in ${enrolment.batchCode}?`;
  }

  get dialogConfirmLabel(): string {
    return this.pendingAction?.type === 'drop' ? 'Drop Student' : 'Delete';
  }

  ngOnInit(): void {
    this.loadEnrolments();
  }

  loadEnrolments(): void {
    this.loading = true;
    this.errorMessage = '';
    this.enrolmentService.getAll().subscribe({
      next: (data) => {
        this.enrolments = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }

  onSearchChange(value: string): void {
    this.searchTerm = value;
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  private applyFilters(): void {
    const query = this.searchTerm.trim().toLowerCase();
    this.filteredEnrolments = this.enrolments.filter((item) => {
      const matchesStatus =
        this.statusFilter === 'ALL' || item.status === this.statusFilter;
      const matchesQuery =
        !query ||
        item.studentCode.toLowerCase().includes(query) ||
        item.studentName.toLowerCase().includes(query) ||
        item.studentEmail.toLowerCase().includes(query) ||
        item.batchCode.toLowerCase().includes(query) ||
        item.batchName.toLowerCase().includes(query) ||
        item.courseTitle.toLowerCase().includes(query);
      return matchesStatus && matchesQuery;
    });
  }

  requestAction(type: 'delete' | 'drop', enrolment: Enrolment): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.pendingAction = { type, enrolment };
  }

  cancelAction(): void {
    this.pendingAction = null;
  }

  confirmAction(): void {
    if (!this.pendingAction) {
      return;
    }
    this.processing = true;
    const { type, enrolment } = this.pendingAction;

    if (type === 'delete') {
      this.enrolmentService.delete(enrolment.id).subscribe({
        next: () => {
          this.processing = false;
          this.pendingAction = null;
          this.successMessage = `Enrolment for "${enrolment.studentName}" deleted successfully.`;
          this.loadEnrolments();
        },
        error: (err: Error) => {
          this.processing = false;
          this.pendingAction = null;
          this.errorMessage = err.message;
        }
      });
      return;
    }

    const payload: EnrolmentRequest = {
      studentId: enrolment.studentId,
      batchId: enrolment.batchId,
      enrolmentDate: enrolment.enrolmentDate,
      status: 'DROPPED',
      attendancePercentage: enrolment.attendancePercentage,
      remarks: enrolment.remarks
    };

    this.enrolmentService.update(enrolment.id, payload).subscribe({
      next: () => {
        this.processing = false;
        this.pendingAction = null;
        this.successMessage = `Enrolment for "${enrolment.studentName}" marked as DROPPED.`;
        this.loadEnrolments();
      },
      error: (err: Error) => {
        this.processing = false;
        this.pendingAction = null;
        this.errorMessage = err.message;
      }
    });
  }
}
