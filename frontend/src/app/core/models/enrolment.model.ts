import { StudentStatus } from './student.model';

export type EnrolmentStatus = 'ENROLLED' | 'COMPLETED' | 'DROPPED';

export interface Enrolment {
  id: number;
  studentId: number;
  studentCode: string;
  studentName: string;
  studentEmail: string;
  studentStatus: StudentStatus;
  batchId: number;
  batchCode: string;
  batchName: string;
  courseTitle: string;
  enrolmentDate: string;
  status: EnrolmentStatus;
  attendancePercentage: number;
  remarks: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface EnrolmentRequest {
  studentId: number;
  batchId: number;
  enrolmentDate: string | null;
  status: EnrolmentStatus;
  attendancePercentage: number | null;
  remarks: string | null;
}

export interface AttendanceUpdateRequest {
  attendancePercentage: number;
  remarks: string | null;
}
