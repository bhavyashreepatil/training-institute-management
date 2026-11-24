import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

const STATUS_CLASS_MAP: Record<string, string> = {
  ACTIVE: 'text-bg-success',
  INACTIVE: 'text-bg-secondary',
  GRADUATED: 'text-bg-primary',
  DROPPED: 'text-bg-danger',
  UPCOMING: 'text-bg-info',
  ONGOING: 'text-bg-success',
  COMPLETED: 'text-bg-primary',
  CANCELLED: 'text-bg-danger',
  ENROLLED: 'text-bg-success',
  ONLINE: 'text-bg-info',
  OFFLINE: 'text-bg-warning',
  HYBRID: 'text-bg-primary'
};

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input({ required: true }) status: string | boolean = '';
  @Input() activeLabel = 'Active';
  @Input() inactiveLabel = 'Inactive';

  get displayText(): string {
    if (typeof this.status === 'boolean') {
      return this.status ? this.activeLabel : this.inactiveLabel;
    }
    return this.status;
  }

  get badgeClass(): string {
    if (typeof this.status === 'boolean') {
      return this.status ? 'text-bg-success' : 'text-bg-secondary';
    }
    return STATUS_CLASS_MAP[this.status.toUpperCase()] ?? 'text-bg-secondary';
  }
}
