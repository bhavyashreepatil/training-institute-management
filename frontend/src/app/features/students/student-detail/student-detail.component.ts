import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Enrolment } from '../../../core/models/enrolment.model';
import { Student } from '../../../core/models/student.model';
import { EnrolmentService } from '../../../core/services/enrolment.service';
import { StudentService } from '../../../core/services/student.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-student-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent
  ],
  templateUrl: './student-detail.component.html',
  styleUrl: './student-detail.component.scss'
})
export class StudentDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly studentService = inject(StudentService);
  private readonly enrolmentService = inject(EnrolmentService);

  student: Student | null = null;
  enrolments: Enrolment[] = [];
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      student: this.studentService.getById(id),
      enrolments: this.enrolmentService.getAll({ studentId: id })
    }).subscribe({
      next: ({ student, enrolments }) => {
        this.student = student;
        this.enrolments = enrolments;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }
}
