import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Batch } from '../../../core/models/batch.model';
import { Course } from '../../../core/models/course.model';
import { BatchService } from '../../../core/services/batch.service';
import { CourseService } from '../../../core/services/course.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent,
    StatusBadgeComponent
  ],
  templateUrl: './course-detail.component.html',
  styleUrl: './course-detail.component.scss'
})
export class CourseDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly courseService = inject(CourseService);
  private readonly batchService = inject(BatchService);

  course: Course | null = null;
  batches: Batch[] = [];
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      course: this.courseService.getById(id),
      batches: this.batchService.getAll({ courseId: id })
    }).subscribe({
      next: ({ course, batches }) => {
        this.course = course;
        this.batches = batches;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }
}
