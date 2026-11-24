# Test Report

## 1. Backend Verification Summary

**Test Framework:** JUnit 5, Mockito, Spring Boot Test (`@DataJpaTest`, `@WebMvcTest`), H2 In-Memory Database (`application-test.yml`)  
**Total Backend Tests:** **52**  
**Result:** **52 Passed, 0 Failed, 0 Skipped**

### Backend Test Suites (`backend/src/test/java/com/institute/management/`)
| Layer | Test Class | Coverage |
|---|---|---|
| Application | `InstituteManagementApplicationTests` | Spring context load smoke test |
| Repository (`@DataJpaTest`) | `CourseRepositoryTest` | Unique `course_code`, active/search queries |
| Repository (`@DataJpaTest`) | `TrainerRepositoryTest` | Unique `employee_code` & `email`, active/search queries |
| Repository (`@DataJpaTest`) | `StudentRepositoryTest` | Unique `student_code` & `email`, status/search queries |
| Repository (`@DataJpaTest`) | `BatchRepositoryTest` | Unique `batch_code`, course/trainer/status queries |
| Repository (`@DataJpaTest`) | `EnrolmentRepositoryTest` | Composite unique `(student_id, batch_id)`, attendance aggregation |
| Service (Mockito) | `CourseServiceTest` | CRUD, duplicate code check, delete protection |
| Service (Mockito) | `TrainerServiceTest` | CRUD, duplicate employee code/email, delete protection |
| Service (Mockito) | `StudentServiceTest` | CRUD, duplicate student code/email, delete protection |
| Service (Mockito) | `BatchServiceTest` | Active course/trainer guards, `endDate >= startDate`, seat counts, roster |
| Service (Mockito) | `EnrolmentServiceTest` | Capacity exceeded (`409`), inactive student (`409`), duplicate enrolment (`409`), attendance updates |
| Service (Mockito) | `DashboardServiceTest` | Live KPI counts, seat utilization, average attendance |
| Controller (`MockMvc`) | `CourseControllerTest` | `/api/v1/courses` endpoints & validation |
| Controller (`MockMvc`) | `TrainerControllerTest` | `/api/v1/trainers` endpoints & validation |
| Controller (`MockMvc`) | `StudentControllerTest` | `/api/v1/students` endpoints & validation |
| Controller (`MockMvc`) | `BatchControllerTest` | `/api/v1/batches` & `/roster` endpoints & validation |
| Controller (`MockMvc`) | `EnrolmentControllerTest` | `/api/v1/enrolments` & `/attendance` endpoints & `409` responses |
| Controller (`MockMvc`) | `DashboardControllerTest` | `/api/v1/dashboard/summary` endpoint |

---

## 2. Frontend Verification Summary

**Build Command (`frontend/`):** `npm.cmd run build`  
**Build Result:** **Passed** (`Application bundle generation complete`, zero TypeScript or template errors; initial bundle 710.15 kB with standard 500 kB budget warning).

**Test Command (`frontend/`):** `npx.cmd ng test --watch=false --browsers=ChromeHeadless`  
**Test Result:** **1 / 1 Passed** (`App` root component spec executed in Chrome Headless 157.0.0.0).

### Implemented Feature Modules Verified in Build
- `DashboardComponent` (`/dashboard`)
- `CourseListComponent`, `CourseFormComponent`, `CourseDetailComponent` (`/courses/*`)
- `TrainerListComponent`, `TrainerFormComponent`, `TrainerDetailComponent` (`/trainers/*`)
- `StudentListComponent`, `StudentFormComponent`, `StudentDetailComponent` (`/students/*`)
- `BatchListComponent`, `BatchFormComponent`, `BatchDetailComponent`, `BatchRosterComponent` (`/batches/*`)
- `EnrolmentListComponent`, `EnrolmentFormComponent`, `EnrolmentDetailComponent` (`/enrolments/*`)

---

## 3. Live End-to-End Verification Checklist (Phase 6.3)

Run MySQL, Spring Boot (`http://localhost:8080`), and Angular (`http://localhost:4200`) and verify:
- [ ] `GET http://localhost:8080/api/v1/dashboard/summary` returns live JSON metrics from `institute_db`.
- [ ] Creating a Course, Trainer, Student, Batch, and Enrolment in the Angular UI persists to MySQL and updates Dashboard KPIs.
- [ ] Attempting to enrol beyond `maxCapacity` displays the backend `409 Conflict` error cleanly in the Enrolment Form alert banner.
- [ ] Updating attendance % inline in Batch Roster (`PATCH /api/v1/enrolments/{id}/attendance`) persists and updates the batch average attendance.
