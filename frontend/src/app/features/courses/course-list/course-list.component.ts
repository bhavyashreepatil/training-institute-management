import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Course } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';
import { ConfirmationDialogComponent } from '../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { DataTableColumn, DataTableComponent } from '../../../shared/components/data-table/data-table.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { SearchBarComponent } from '../../../shared/components/search-bar/search-bar.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-course-list',
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
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.scss'
})
export class CourseListComponent implements OnInit {
  private readonly courseService = inject(CourseService);

  readonly columns: readonly DataTableColumn[] = [
    { key: 'courseCode', label: 'Code' },
    { key: 'title', label: 'Title' },
    { key: 'durationWeeks', label: 'Duration' },
    { key: 'fee', label: 'Fee' },
    { key: 'active', label: 'Status' },
    { key: 'actions', label: 'Actions', headerClass: 'text-end' }
  ];

  courses: Course[] = [];
  searchTerm = '';
  statusFilter: 'ALL' | 'ACTIVE' | 'INACTIVE' = 'ALL';
  loading = true;
  errorMessage = '';
  successMessage = '';
  courseToDelete: Course | null = null;
  deleting = false;

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.loading = true;
    this.errorMessage = '';
    const activeOnly = this.statusFilter === 'ACTIVE' ? true : undefined;
    this.courseService.getAll(this.searchTerm, activeOnly).subscribe({
      next: (data) => {
        this.courses =
          this.statusFilter === 'INACTIVE' ? data.filter((item) => !item.active) : data;
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
    this.loadCourses();
  }

  onFilterChange(): void {
    this.loadCourses();
  }

  requestDelete(course: Course): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.courseToDelete = course;
  }

  cancelDelete(): void {
    this.courseToDelete = null;
  }

  confirmDelete(): void {
    if (!this.courseToDelete) {
      return;
    }
    this.deleting = true;
    const target = this.courseToDelete;
    this.courseService.delete(target.id).subscribe({
      next: () => {
        this.deleting = false;
        this.courseToDelete = null;
        this.successMessage = `Course "${target.title}" deleted successfully.`;
        this.loadCourses();
      },
      error: (err: Error) => {
        this.deleting = false;
        this.courseToDelete = null;
        this.errorMessage = err.message;
      }
    });
  }
}
