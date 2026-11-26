declare global {
  interface Window {
    __APP_CONFIG__?: {
      apiBaseUrl?: string;
    };
  }
}

export const API_BASE_URL = window.__APP_CONFIG__?.apiBaseUrl ?? 'http://localhost:8080/api/v1';

export const API_ENDPOINTS = {
  COURSES: `${API_BASE_URL}/courses`,
  TRAINERS: `${API_BASE_URL}/trainers`,
  STUDENTS: `${API_BASE_URL}/students`,
  BATCHES: `${API_BASE_URL}/batches`,
  ENROLMENTS: `${API_BASE_URL}/enrolments`,
  DASHBOARD_SUMMARY: `${API_BASE_URL}/dashboard/summary`
} as const;
