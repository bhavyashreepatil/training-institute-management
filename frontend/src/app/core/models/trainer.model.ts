export interface Trainer {
  id: number;
  employeeCode: string;
  fullName: string;
  email: string;
  phone: string;
  specialization: string;
  experienceYears: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface TrainerRequest {
  employeeCode: string;
  fullName: string;
  email: string;
  phone: string;
  specialization: string;
  experienceYears: number;
  active: boolean;
}
