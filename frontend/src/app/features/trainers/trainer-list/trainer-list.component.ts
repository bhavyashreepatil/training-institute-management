import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Trainer } from '../../../core/models/trainer.model';
import { TrainerService } from '../../../core/services/trainer.service';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-trainer-list',
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
  templateUrl: './trainer-list.component.html',
  styleUrl: './trainer-list.component.scss'
})
export class TrainerListComponent implements OnInit {
  private readonly trainerService = inject(TrainerService);

  readonly columns: readonly DataTableColumn[] = [
    { key: 'employeeCode', label: 'Code' },
    { key: 'fullName', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'specialization', label: 'Specialization' },
    { key: 'experienceYears', label: 'Exp. (yrs)' },
    { key: 'active', label: 'Status' },
    { key: 'actions', label: 'Actions', headerClass: 'text-end' }
  ];

  trainers: Trainer[] = [];
  searchTerm = '';
  statusFilter: 'ALL' | 'ACTIVE' | 'INACTIVE' = 'ALL';
  loading = true;
  errorMessage = '';
  successMessage = '';
  trainerToDelete: Trainer | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadTrainers();
  }

  loadTrainers(): void {
    this.loading = true;
    this.errorMessage = '';
    const activeOnly = this.statusFilter === 'ACTIVE' ? true : undefined;
    this.trainerService.getAll(this.searchTerm, activeOnly).subscribe({
      next: (data) => {
        this.trainers =
          this.statusFilter === 'INACTIVE' ? data.filter((t) => !t.active) : data;
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
    this.loadTrainers();
  }

  onFilterChange(): void {
    this.loadTrainers();
  }

  requestDelete(trainer: Trainer): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.trainerToDelete = trainer;
  }

  cancelDelete(): void {
    this.trainerToDelete = null;
  }

  confirmDelete(): void {
    if (!this.trainerToDelete) {
      return;
    }
    this.deleting = true;
    const target = this.trainerToDelete;
    this.trainerService.delete(target.id).subscribe({
      next: () => {
        this.deleting = false;
        this.trainerToDelete = null;
        this.successMessage = `Trainer "${target.fullName}" deleted successfully.`;
        this.loadTrainers();
      },
      error: (err: Error) => {
        this.deleting = false;
        this.trainerToDelete = null;
        this.errorMessage = err.message;
      }
    });
  }
}
