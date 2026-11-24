import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StudentRequest, StudentStatus } from '../../../core/models/student.model';
import { StudentService } from '../../../core/services/student.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, LoadingIndicatorComponent],
  templateUrl: './student-form.component.html',
  styleUrl: './student-form.component.scss'
})
export class StudentFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly studentService = inject(StudentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly statusOptions: Array<{ value: StudentStatus; label: string }> = [
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'GRADUATED', label: 'Graduated' },
    { value: 'DROPPED', label: 'Dropped' }
  ];

  readonly form = this.fb.nonNullable.group({
    studentCode: ['', [Validators.required, Validators.maxLength(20)]],
    fullName: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    phone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9]{10,15}$/)]],
    dateOfBirth: this.fb.control<string | null>(null),
    status: this.fb.control<StudentStatus>('ACTIVE', Validators.required)
  });

  studentId: number | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.studentId !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.studentId = Number(idParam);
      this.loadStudent(this.studentId);
    }
  }

  loadStudent(id: number): void {
    this.loading = true;
    this.studentService.getById(id).subscribe({
      next: (student) => {
        this.form.patchValue({
          studentCode: student.studentCode,
          fullName: student.fullName,
          email: student.email,
          phone: student.phone,
          dateOfBirth: student.dateOfBirth,
          status: student.status
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
    const payload: StudentRequest = {
      studentCode: raw.studentCode.trim(),
      fullName: raw.fullName.trim(),
      email: raw.email.trim(),
      phone: raw.phone.trim(),
      dateOfBirth: raw.dateOfBirth || null,
      status: raw.status ?? 'ACTIVE'
    };

    const request$ =
      this.isEditMode && this.studentId !== null
        ? this.studentService.update(this.studentId, payload)
        : this.studentService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.submitting = false;
        void this.router.navigate(['/students', saved.id]);
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
