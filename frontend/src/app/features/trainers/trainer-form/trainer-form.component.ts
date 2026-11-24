import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TrainerRequest } from '../../../core/models/trainer.model';
import { TrainerService } from '../../../core/services/trainer.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';

@Component({
  selector: 'app-trainer-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, LoadingIndicatorComponent],
  templateUrl: './trainer-form.component.html',
  styleUrl: './trainer-form.component.scss'
})
export class TrainerFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly trainerService = inject(TrainerService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly form = this.fb.nonNullable.group({
    employeeCode: ['', [Validators.required, Validators.maxLength(20)]],
    fullName: ['', [Validators.required, Validators.maxLength(120)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    phone: ['', [Validators.required, Validators.pattern(/^[+]?[0-9]{10,15}$/)]],
    specialization: ['', [Validators.required, Validators.maxLength(120)]],
    experienceYears: [0, [Validators.required, Validators.min(0)]],
    active: [true]
  });

  trainerId: number | null = null;
  loading = false;
  submitting = false;
  errorMessage = '';

  get isEditMode(): boolean {
    return this.trainerId !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.trainerId = Number(idParam);
      this.loadTrainer(this.trainerId);
    }
  }

  loadTrainer(id: number): void {
    this.loading = true;
    this.trainerService.getById(id).subscribe({
      next: (trainer) => {
        this.form.patchValue({
          employeeCode: trainer.employeeCode,
          fullName: trainer.fullName,
          email: trainer.email,
          phone: trainer.phone,
          specialization: trainer.specialization,
          experienceYears: trainer.experienceYears,
          active: trainer.active
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
    const payload: TrainerRequest = {
      employeeCode: raw.employeeCode.trim(),
      fullName: raw.fullName.trim(),
      email: raw.email.trim(),
      phone: raw.phone.trim(),
      specialization: raw.specialization.trim(),
      experienceYears: Number(raw.experienceYears),
      active: raw.active
    };

    const request$ =
      this.isEditMode && this.trainerId !== null
        ? this.trainerService.update(this.trainerId, payload)
        : this.trainerService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.submitting = false;
        void this.router.navigate(['/trainers', saved.id]);
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
