import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { BatchMode, BatchRequest, BatchStatus } from '../../../core/models/batch.model';
import { Course } from '../../../core/models/course.model';
import { Trainer } from '../../../core/models/trainer.model';
import { BatchService } from '../../../core/services/batch.service';
import { CourseService } from '../../../core/services/course.service';
import { TrainerService } from '../../../core/services/trainer.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

const dateRangeValidator: ValidatorFn = (group: AbstractControl): ValidationErrors | null => {
  const startDate = group.get('startDate')?.value as string | undefined;
  const endDate = group.get('endDate')?.value as string | undefined;
  if (startDate && endDate && endDate < startDate) {
    return { dateRangeInvalid: true };
  }
  return null;
};

@Component({
  selector: 'app-batch-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, LoadingIndicatorComponent],
  templateUrl: './batch-form.component.html',
  styleUrl: './batch-form.component.scss'
})
export class BatchFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly batchService = inject(BatchService);
  private readonly courseService = inject(CourseService);
  private readonly trainerService = inject(TrainerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly modes: readonly BatchMode[] = ['ONLINE', 'OFFLINE', 'HYBRID'];
  readonly statuses: readonly BatchStatus[] = ['UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'];

  readonly form = this.fb.nonNullable.group(
    {
      batchCode: ['', [Validators.required, Validators.maxLength(20)]],
      batchName: ['', [Validators.required, Validators.maxLength(150)]],
      courseId: [0, [Validators.required, Validators.min(1)]],
      trainerId: [0, [Validators.required, Validators.min(1)]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      scheduleTimings: ['', [Validators.required, Validators.maxLength(100)]],
      mode: ['ONLINE' as BatchMode, [Validators.required]],
      maxCapacity: [20, [Validators.required, Validators.min(1)]],
      status: ['UPCOMING' as BatchStatus, [Validators.required]]
    },
    { validators: dateRangeValidator }
  );

  courses: Course[] = [];
  trainers: Trainer[] = [];
  batchId: number | null = null;
  loading = true;
  submitting = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.batchId !== null;
  }

  get hasDateRangeError(): boolean {
    return (
      Boolean(this.form.errors?.['dateRangeInvalid']) &&
      (this.form.controls.endDate.dirty || this.form.controls.endDate.touched)
    );
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.batchId = Number(idParam);
    }
    this.loadInitialData();
  }

  private loadInitialData(): void {
    this.loading = true;
    const courses$ = this.courseService.getAll(undefined, true);
    const trainers$ = this.trainerService.getAll(undefined, true);

    if (this.batchId !== null) {
      forkJoin({
        courses: courses$,
        trainers: trainers$,
        batch: this.batchService.getById(this.batchId)
      }).subscribe({
        next: ({ courses, trainers, batch }) => {
          this.courses = courses;
          this.trainers = trainers;
          this.form.patchValue({
            batchCode: batch.batchCode,
            batchName: batch.batchName,
            courseId: batch.courseId,
            trainerId: batch.trainerId,
            startDate: batch.startDate,
            endDate: batch.endDate,
            scheduleTimings: batch.scheduleTimings,
            mode: batch.mode,
            maxCapacity: batch.maxCapacity,
            status: batch.status
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
        courses: courses$,
        trainers: trainers$
      }).subscribe({
        next: ({ courses, trainers }) => {
          this.courses = courses;
          this.trainers = trainers;
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
    const payload: BatchRequest = {
      batchCode: raw.batchCode.trim(),
      batchName: raw.batchName.trim(),
      courseId: Number(raw.courseId),
      trainerId: Number(raw.trainerId),
      startDate: raw.startDate,
      endDate: raw.endDate,
      scheduleTimings: raw.scheduleTimings.trim(),
      mode: raw.mode,
      maxCapacity: Number(raw.maxCapacity),
      status: raw.status
    };

    const request$ =
      this.isEditMode && this.batchId !== null
        ? this.batchService.update(this.batchId, payload)
        : this.batchService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.submitting = false;
        void this.router.navigate(['/batches', saved.id]);
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
