import { Enrolment } from './enrolment.model';

export type BatchMode = 'ONLINE' | 'OFFLINE' | 'HYBRID';
export type BatchStatus = 'UPCOMING' | 'ONGOING' | 'COMPLETED' | 'CANCELLED';

export interface Batch {
  id: number;
  batchCode: string;
  batchName: string;
  courseId: number;
  courseCode: string;
  courseTitle: string;
  trainerId: number;
  trainerName: string;
  trainerEmployeeCode: string;
  startDate: string;
  endDate: string;
  scheduleTimings: string;
  mode: BatchMode;
  maxCapacity: number;
  enrolledCount: number;
  availableSeats: number;
  status: BatchStatus;
  createdAt: string;
  updatedAt: string;
}

export interface BatchRequest {
  batchCode: string;
  batchName: string;
  courseId: number;
  trainerId: number;
  startDate: string;
  endDate: string;
  scheduleTimings: string;
  mode: BatchMode;
  maxCapacity: number;
  status: BatchStatus;
}

export interface BatchRoster {
  batch: Batch;
  averageAttendancePercentage: number;
  enrolments: Enrolment[];
}
