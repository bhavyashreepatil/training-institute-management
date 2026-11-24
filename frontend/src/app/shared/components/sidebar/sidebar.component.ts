import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

export interface SidebarNavItem {
  label: string;
  route: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() isOpen = false;
  @Output() closeSidebar = new EventEmitter<void>();

  readonly navItems: readonly SidebarNavItem[] = [
    { label: 'Dashboard', route: '/dashboard', icon: 'bi-speedometer2' },
    { label: 'Courses', route: '/courses', icon: 'bi-book' },
    { label: 'Trainers', route: '/trainers', icon: 'bi-person-badge' },
    { label: 'Students', route: '/students', icon: 'bi-people' },
    { label: 'Batches', route: '/batches', icon: 'bi-calendar2-week' },
    { label: 'Enrolments', route: '/enrolments', icon: 'bi-clipboard-check' }
  ];

  onNavigate(): void {
    this.closeSidebar.emit();
  }
}
