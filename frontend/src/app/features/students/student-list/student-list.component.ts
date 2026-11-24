import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Student, StudentStatus } from '../../../core/models/student.model';
import { StudentService } from '../../../core/services/student.service';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    SearchBarComponent,
    DataTableComponent,
    ConfirmationDialogComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent
  ],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.scss'
})
export class StudentListComponent implements OnInit {
  private readonly studentService = inject(StudentService);

  readonly columns: readonly DataTableColumn[] = [
    { key: 'studentCode', label: 'Code' },
    { key: 'fullName', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Phone' },
    { key: 'dateOfBirth', label: 'DOB' },
    { key: 'status', label: 'Status' },
    { key: 'actions', label: 'Actions', headerClass: 'text-end' }
  ];

  readonly statusOptions: Array<{ value: StudentStatus | 'ALL'; label: string }> = [
    { value: 'ALL', label: 'All Statuses' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'GRADUATED', label: 'Graduated' },
    { value: 'DROPPED', label: 'Dropped' }
  ];

  students: Student[] = [];
  searchTerm = '';
  statusFilter: StudentStatus | 'ALL' = 'ALL';
  loading = true;
  errorMessage = '';
  successMessage = '';
  studentToDelete: Student | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.loading = true;
    this.errorMessage = '';
    const status = this.statusFilter === 'ALL' ? undefined : this.statusFilter;
    this.studentService.getAll(this.searchTerm, status).subscribe({
      next: (data) => {
        this.students = data;
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
    this.loadStudents();
  }

  onFilterChange(): void {
    this.loadStudents();
  }

  requestDelete(student: Student): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.studentToDelete = student;
  }

  cancelDelete(): void {
    this.studentToDelete = null;
  }

  confirmDelete(): void {
    if (!this.studentToDelete) {
      return;
    }
    this.deleting = true;
    const target = this.studentToDelete;
    this.studentService.delete(target.id).subscribe({
      next: () => {
        this.deleting = false;
        this.studentToDelete = null;
        this.successMessage = `Student "${target.fullName}" deleted successfully.`;
        this.loadStudents();
      },
      error: (err: Error) => {
        this.deleting = false;
        this.studentToDelete = null;
        this.errorMessage = err.message;
      }
    });
  }
}
