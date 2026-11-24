import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Batch, BatchStatus } from '../../../core/models/batch.model';
import { BatchService } from '../../../core/services/batch.service';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-batch-list',
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
  templateUrl: './batch-list.component.html',
  styleUrl: './batch-list.component.scss'
})
export class BatchListComponent implements OnInit {
  private readonly batchService = inject(BatchService);

  readonly columns: readonly DataTableColumn[] = [
    { key: 'batchCode', label: 'Code' },
    { key: 'batchName', label: 'Batch' },
    { key: 'courseTitle', label: 'Course' },
    { key: 'trainerName', label: 'Trainer' },
    { key: 'dates', label: 'Schedule Dates' },
    { key: 'seats', label: 'Seats' },
    { key: 'mode', label: 'Mode' },
    { key: 'status', label: 'Status' },
    { key: 'actions', label: 'Actions', headerClass: 'text-end' }
  ];

  batches: Batch[] = [];
  filteredBatches: Batch[] = [];
  searchTerm = '';
  statusFilter: 'ALL' | BatchStatus = 'ALL';
  loading = true;
  errorMessage = '';
  successMessage = '';
  batchToDelete: Batch | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadBatches();
  }

  loadBatches(): void {
    this.loading = true;
    this.errorMessage = '';
    const status = this.statusFilter === 'ALL' ? undefined : this.statusFilter;
    this.batchService.getAll({ status }).subscribe({
      next: (data) => {
        this.batches = data;
        this.applySearchFilter();
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
    this.applySearchFilter();
  }

  onFilterChange(): void {
    this.loadBatches();
  }

  private applySearchFilter(): void {
    const query = this.searchTerm.trim().toLowerCase();
    if (!query) {
      this.filteredBatches = this.batches;
      return;
    }
    this.filteredBatches = this.batches.filter(
      (batch) =>
        batch.batchCode.toLowerCase().includes(query) ||
        batch.batchName.toLowerCase().includes(query) ||
        batch.courseTitle.toLowerCase().includes(query) ||
        batch.trainerName.toLowerCase().includes(query)
    );
  }

  seatUtilizationPercent(batch: Batch): number {
    if (batch.maxCapacity <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((batch.enrolledCount / batch.maxCapacity) * 100));
  }

  seatBarClass(batch: Batch): string {
    const pct = this.seatUtilizationPercent(batch);
    if (pct >= 100) {
      return 'bg-danger';
    }
    if (pct >= 80) {
      return 'bg-warning';
    }
    return 'bg-success';
  }

  requestDelete(batch: Batch): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.batchToDelete = batch;
  }

  cancelDelete(): void {
    this.batchToDelete = null;
  }

  confirmDelete(): void {
    if (!this.batchToDelete) {
      return;
    }
    this.deleting = true;
    const target = this.batchToDelete;
    this.batchService.delete(target.id).subscribe({
      next: () => {
        this.deleting = false;
        this.batchToDelete = null;
        this.successMessage = `Batch "${target.batchName}" deleted successfully.`;
        this.loadBatches();
      },
      error: (err: Error) => {
        this.deleting = false;
        this.batchToDelete = null;
        this.errorMessage = err.message;
      }
    });
  }
}
