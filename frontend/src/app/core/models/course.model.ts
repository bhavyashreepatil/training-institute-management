export interface Course {
  id: number;
  courseCode: string;
  title: string;
  description: string | null;
  durationWeeks: number;
  fee: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CourseRequest {
  courseCode: string;
  title: string;
  description: string | null;
  durationWeeks: number;
  fee: number;
  active: boolean;
}
