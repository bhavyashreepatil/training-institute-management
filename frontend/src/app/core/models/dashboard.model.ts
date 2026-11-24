import { Batch } from './batch.model';

export interface DashboardSummary {
  totalCourses: number;
  activeCourses: number;
  totalTrainers: number;
  activeTrainers: number;
  totalStudents: number;
  activeStudents: number;
  totalBatches: number;
  activeBatches: number;
  totalEnrolments: number;
  activeEnrolments: number;
  totalSeatCapacity: number;
  totalEnrolledSeats: number;
  seatUtilizationPercentage: number;
  averageAttendancePercentage: number;
  activeBatchList: Batch[];
}
