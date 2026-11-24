import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Batch } from '../../../core/models/batch.model';
import { Trainer } from '../../../core/models/trainer.model';
import { BatchService } from '../../../core/services/batch.service';
import { TrainerService } from '../../../core/services/trainer.service';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { LoadingIndicatorComponent } from '../../../shared/components/loading-indicator/loading-indicator.component';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { StatusBadgeComponent } from '../../../shared/components/status-badge/status-badge.component';

@Component({
  selector: 'app-trainer-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    PageHeaderComponent,
    LoadingIndicatorComponent,
    EmptyStateComponent,
    StatusBadgeComponent
  ],
  templateUrl: './trainer-detail.component.html',
  styleUrl: './trainer-detail.component.scss'
})
export class TrainerDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly trainerService = inject(TrainerService);
  private readonly batchService = inject(BatchService);

  trainer: Trainer | null = null;
  batches: Batch[] = [];
  loading = true;
  errorMessage = '';

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      trainer: this.trainerService.getById(id),
      batches: this.batchService.getAll({ trainerId: id })
    }).subscribe({
      next: ({ trainer, batches }) => {
        this.trainer = trainer;
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
