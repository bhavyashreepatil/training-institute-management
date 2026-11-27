export const API_BASE_URL = 'https://training-institute-management.onrender.com/api/v1';

export const API_ENDPOINTS = {
  COURSES: `${API_BASE_URL}/courses`,
  TRAINERS: `${API_BASE_URL}/trainers`,
  STUDENTS: `${API_BASE_URL}/students`,
  BATCHES: `${API_BASE_URL}/batches`,
  ENROLMENTS: `${API_BASE_URL}/enrolments`,
  DASHBOARD_SUMMARY: `${API_BASE_URL}/dashboard/summary`
} as const;
