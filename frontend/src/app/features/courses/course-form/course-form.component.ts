import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CourseRequest } from '../../../core/models/course.model';
import { CourseService } from '../../../core/services/course.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-course-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, LoadingIndicatorComponent],
  templateUrl: './course-form.component.html',
  styleUrl: './course-form.component.scss'
})
export class CourseFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly courseService = inject(CourseService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    courseCode: ['', [Validators.required, Validators.maxLength(20)]],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    description: [''],
    durationWeeks: [8, [Validators.required, Validators.min(1)]],
    fee: [0, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  courseId: number | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.courseId !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.courseId = Number(idParam);
      this.loadCourse(this.courseId);
    }
  }

  loadCourse(id: number): void {
    this.loading = true;
    this.courseService.getById(id).subscribe({
      next: (course) => {
        this.form.patchValue({
          courseCode: course.courseCode,
          title: course.title,
          description: course.description ?? '',
          durationWeeks: course.durationWeeks,
          fee: course.fee,
          active: course.active
        });
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting = true;
    this.errorMessage = '';
    const raw = this.form.getRawValue();
    const payload: CourseRequest = {
      courseCode: raw.courseCode.trim(),
      title: raw.title.trim(),
      description: raw.description.trim() || null,
      durationWeeks: Number(raw.durationWeeks),
      fee: Number(raw.fee),
      active: raw.active
    };

    const request$ = this.isEditMode && this.courseId !== null
      ? this.courseService.update(this.courseId, payload)
      : this.courseService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.submitting = false;
        void this.router.navigate(['/courses', saved.id]);
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
