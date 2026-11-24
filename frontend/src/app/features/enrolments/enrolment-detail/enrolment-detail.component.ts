import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Enrolment } from '../../../core/models/enrolment.model';
import { EnrolmentService } from '../../../core/services/enrolment.service';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-enrolment-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    StatusBadgeComponent
  ],
  templateUrl: './enrolment-detail.component.html',
  styleUrl: './enrolment-detail.component.scss'
})
export class EnrolmentDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly enrolmentService = inject(EnrolmentService);

  enrolment: Enrolment | null = null;
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.enrolmentService.getById(id).subscribe({
      next: (data) => {
        this.enrolment = data;
        this.loading = false;
      },
      error: (err: Error) => {
        this.errorMessage = err.message;
        this.loading = false;
      }
    });
  }
}
