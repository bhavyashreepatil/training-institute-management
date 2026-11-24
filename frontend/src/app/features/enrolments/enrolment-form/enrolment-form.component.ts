import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Batch } from '../../../core/models/batch.model';
import { EnrolmentRequest, EnrolmentStatus } from '../../../core/models/enrolment.model';
import { Student } from '../../../core/models/student.model';
import { BatchService } from '../../../core/services/batch.service';
import { EnrolmentService } from '../../../core/services/enrolment.service';
import { StudentService } from '../../../core/services/student.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-enrolment-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    StatusBadgeComponent
  ],
  templateUrl: './enrolment-form.component.html',
  styleUrl: './enrolment-form.component.scss'
})
export class EnrolmentFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly enrolmentService = inject(EnrolmentService);
  private readonly studentService = inject(StudentService);
  private readonly batchService = inject(BatchService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly statuses: readonly EnrolmentStatus[] = ['ENROLLED', 'COMPLETED', 'DROPPED'];

  readonly form = this.fb.nonNullable.group({
    studentId: [0, [Validators.required, Validators.min(1)]],
    batchId: [0, [Validators.required, Validators.min(1)]],
    enrolmentDate: [new Date().toISOString().slice(0, 10), [Validators.required]],
    status: ['ENROLLED' as EnrolmentStatus, [Validators.required]],
    attendancePercentage: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
    remarks: ['', [Validators.maxLength(255)]]
  });

  students: Student[] = [];
  batches: Batch[] = [];
  enrolmentId: number | null = null;
  loading = true;
  submitting = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.enrolmentId !== null;
  }

  get selectedBatch(): Batch | undefined {
    const batchId = Number(this.form.controls.batchId.value);
    return this.batches.find((b) => b.id === batchId);
  }

  get selectedStudent(): Student | undefined {
    const studentId = Number(this.form.controls.studentId.value);
    return this.students.find((s) => s.id === studentId);
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.enrolmentId = Number(idParam);
    }
    this.loadInitialData();
  }

  private loadInitialData(): void {
    this.loading = true;
    const students$ = this.studentService.getAll();
    const batches$ = this.batchService.getAll();

    if (this.enrolmentId !== null) {
      forkJoin({
        students: students$,
        batches: batches$,
        enrolment: this.enrolmentService.getById(this.enrolmentId)
      }).subscribe({
        next: ({ students, batches, enrolment }) => {
          this.students = students;
          this.batches = batches;
          this.form.patchValue({
            studentId: enrolment.studentId,
            batchId: enrolment.batchId,
            enrolmentDate: enrolment.enrolmentDate,
            status: enrolment.status,
            attendancePercentage: enrolment.attendancePercentage,
            remarks: enrolment.remarks ?? ''
          });
          this.loading = false;
        },
        error: (err: Error) => {
          this.errorMessage = err.message;
          this.loading = false;
        }
      });
    } else {
      forkJoin({
        students: students$,
        batches: batches$
      }).subscribe({
        next: ({ students, batches }) => {
          this.students = students;
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

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    const raw = this.form.getRawValue();
    const payload: EnrolmentRequest = {
      studentId: Number(raw.studentId),
      batchId: Number(raw.batchId),
      enrolmentDate: raw.enrolmentDate || null,
      status: raw.status,
      attendancePercentage: Number(raw.attendancePercentage),
      remarks: raw.remarks.trim() || null
    };

    const request$ =
      this.isEditMode && this.enrolmentId !== null
        ? this.enrolmentService.update(this.enrolmentId, payload)
        : this.enrolmentService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.submitting = false;
        void this.router.navigate(['/enrolments', saved.id]);
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.submitting = false;
      }
    });
  }

  hasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.dirty || control.touched);
  }
}
