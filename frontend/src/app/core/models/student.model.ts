export type StudentStatus = 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'DROPPED';

export interface Student {
  id: number;
  studentCode: string;
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth: string | null;
  status: StudentStatus;
  createdAt: string;
  updatedAt: string;
}

export interface StudentRequest {
  studentCode: string;
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth: string | null;
  status: StudentStatus;
}
