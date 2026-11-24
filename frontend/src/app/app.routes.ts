import { Routes } from '@angular/router';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent)
      },
      // Courses
      {
        path: 'courses',
        loadComponent: () =>
          import('./features/courses/course-list/course-list.component').then(
            (m) => m.CourseListComponent
          )
      },
      {
        path: 'courses/new',
        loadComponent: () =>
          import('./features/courses/course-form/course-form.component').then(
            (m) => m.CourseFormComponent
          )
      },
      {
        path: 'courses/:id',
        loadComponent: () =>
          import('./features/courses/course-detail/course-detail.component').then(
            (m) => m.CourseDetailComponent
          )
      },
      {
        path: 'courses/:id/edit',
        loadComponent: () =>
          import('./features/courses/course-form/course-form.component').then(
            (m) => m.CourseFormComponent
          )
      },
      // Trainers
      {
        path: 'trainers',
        loadComponent: () =>
          import('./features/trainers/trainer-list/trainer-list.component').then(
            (m) => m.TrainerListComponent
          )
      },
      {
        path: 'trainers/new',
        loadComponent: () =>
          import('./features/trainers/trainer-form/trainer-form.component').then(
            (m) => m.TrainerFormComponent
          )
      },
      {
        path: 'trainers/:id',
        loadComponent: () =>
          import('./features/trainers/trainer-detail/trainer-detail.component').then(
            (m) => m.TrainerDetailComponent
          )
      },
      {
        path: 'trainers/:id/edit',
        loadComponent: () =>
          import('./features/trainers/trainer-form/trainer-form.component').then(
            (m) => m.TrainerFormComponent
          )
      },
      // Students
      {
        path: 'students',
        loadComponent: () =>
          import('./features/students/student-list/student-list.component').then(
            (m) => m.StudentListComponent
          )
      },
      {
        path: 'students/new',
        loadComponent: () =>
          import('./features/students/student-form/student-form.component').then(
            (m) => m.StudentFormComponent
          )
      },
      {
        path: 'students/:id',
        loadComponent: () =>
          import('./features/students/student-detail/student-detail.component').then(
            (m) => m.StudentDetailComponent
          )
      },
      {
        path: 'students/:id/edit',
        loadComponent: () =>
          import('./features/students/student-form/student-form.component').then(
            (m) => m.StudentFormComponent
          )
      },
      // Batches
      {
        path: 'batches',
        loadComponent: () =>
          import('./features/batches/batch-list/batch-list.component').then(
            (m) => m.BatchListComponent
          )
      },
      {
        path: 'batches/new',
        loadComponent: () =>
          import('./features/batches/batch-form/batch-form.component').then(
            (m) => m.BatchFormComponent
          )
      },
      {
        path: 'batches/:id',
        loadComponent: () =>
          import('./features/batches/batch-detail/batch-detail.component').then(
            (m) => m.BatchDetailComponent
          )
      },
      {
        path: 'batches/:id/edit',
        loadComponent: () =>
          import('./features/batches/batch-form/batch-form.component').then(
            (m) => m.BatchFormComponent
          )
      },
      {
        path: 'batches/:id/roster',
        loadComponent: () =>
          import('./features/batches/batch-roster/batch-roster.component').then(
            (m) => m.BatchRosterComponent
          )
      },
      // Enrolments
      {
        path: 'enrolments',
        loadComponent: () =>
          import('./features/enrolments/enrolment-list/enrolment-list.component').then(
            (m) => m.EnrolmentListComponent
          )
      },
      {
        path: 'enrolments/new',
        loadComponent: () =>
          import('./features/enrolments/enrolment-form/enrolment-form.component').then(
            (m) => m.EnrolmentFormComponent
          )
      },
      {
        path: 'enrolments/:id',
        loadComponent: () =>
          import('./features/enrolments/enrolment-detail/enrolment-detail.component').then(
            (m) => m.EnrolmentDetailComponent
          )
      },
      {
        path: 'enrolments/:id/edit',
        loadComponent: () =>
          import('./features/enrolments/enrolment-form/enrolment-form.component').then(
            (m) => m.EnrolmentFormComponent
          )
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
