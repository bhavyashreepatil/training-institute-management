# REST API Documentation

**Base URL:** `http://localhost:8080/api/v1`  
**Content-Type:** `application/json`

## Standard Error Format (`ApiErrorResponse`)

All `4xx` and `5xx` errors are handled centrally by `GlobalExceptionHandler` (`@RestControllerAdvice`) and return:

```json
{
  "timestamp": "2026-11-25T00:45:00",
  "status": 409,
  "error": "Conflict",
  "message": "Batch capacity reached. Maximum capacity is 20.",
  "path": "/api/v1/enrolments",
  "fieldErrors": {}
}
```

| Status | Meaning |
|---|---|
| `200 OK` | Request succeeded |
| `201 Created` | Resource created |
| `204 No Content` | Resource deleted |
| `400 Bad Request` | Bean validation failure, invalid date range (`endDate < startDate`), or invalid attendance range |
| `404 Not Found` | Requested entity or referenced foreign key does not exist |
| `409 Conflict` | Duplicate identifier (`courseCode`, `employeeCode`, `studentCode`, `batchCode`, `email`), duplicate `(studentId, batchId)` enrolment, batch capacity exceeded, status guard violation, or referential integrity conflict on delete |

---

## 1. Dashboard

### `GET /api/v1/dashboard/summary`
Returns live aggregate institute KPIs and active batches.

**Response (`200 OK`):**
```json
{
  "totalCourses": 5,
  "activeCourses": 4,
  "totalTrainers": 4,
  "activeTrainers": 4,
  "totalStudents": 12,
  "activeStudents": 10,
  "totalBatches": 4,
  "activeBatches": 3,
  "totalEnrolments": 18,
  "activeEnrolments": 16,
  "totalSeatCapacity": 75,
  "totalEnrolledSeats": 16,
  "seatUtilizationPercentage": 21.33,
  "averageAttendancePercentage": 88.5,
  "activeBatchList": []
}
```

---

## 2. Courses (`/api/v1/courses`)

| Method | Endpoint | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/v1/courses` | `search`, `activeOnly` | List courses with optional search and active filter |
| `GET` | `/api/v1/courses/{id}` | — | Get course by ID |
| `POST` | `/api/v1/courses` | — | Create a new course (`201 Created`) |
| `PUT` | `/api/v1/courses/{id}` | — | Update an existing course |
| `DELETE` | `/api/v1/courses/{id}` | — | Delete course (`409` if referenced by batches) |

**Request (`POST` / `PUT`):**
```json
{
  "courseCode": "CRS-JAVA-101",
  "title": "Full-Stack Java & Spring Boot",
  "description": "Core Java 21, Spring Boot 3.5, JPA, and REST APIs.",
  "durationWeeks": 12,
  "fee": 25000.00,
  "active": true
}
```

---

## 3. Trainers (`/api/v1/trainers`)

| Method | Endpoint | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/v1/trainers` | `search`, `activeOnly` | List trainers with optional search and active filter |
| `GET` | `/api/v1/trainers/{id}` | — | Get trainer by ID |
| `POST` | `/api/v1/trainers` | — | Create a new trainer (`201 Created`) |
| `PUT` | `/api/v1/trainers/{id}` | — | Update an existing trainer |
| `DELETE` | `/api/v1/trainers/{id}` | — | Delete trainer (`409` if assigned to batches) |

**Request (`POST` / `PUT`):**
```json
{
  "employeeCode": "TRN-101",
  "fullName": "Ananya Sharma",
  "email": "ananya.sharma@institute.example",
  "phone": "9876543210",
  "specialization": "Java & Spring Boot",
  "experienceYears": 8,
  "active": true
}
```

---

## 4. Students (`/api/v1/students`)

| Method | Endpoint | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/v1/students` | `search`, `status` | List students with optional search and `StudentStatus` (`ACTIVE`, `INACTIVE`, `GRADUATED`, `DROPPED`) |
| `GET` | `/api/v1/students/{id}` | — | Get student by ID |
| `POST` | `/api/v1/students` | — | Register a new student (`201 Created`) |
| `PUT` | `/api/v1/students/{id}` | — | Update student details |
| `DELETE` | `/api/v1/students/{id}` | — | Delete student (`409` if enrolments exist) |

**Request (`POST` / `PUT`):**
```json
{
  "studentCode": "STU-2026-001",
  "fullName": "Aarav Patel",
  "email": "aarav.patel@student.example",
  "phone": "9876501234",
  "dateOfBirth": "2002-08-19",
  "status": "ACTIVE"
}
```

---

## 5. Batches (`/api/v1/batches`)

| Method | Endpoint | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/v1/batches` | `status`, `courseId`, `trainerId` | List batches with computed `enrolledCount` and `availableSeats` |
| `GET` | `/api/v1/batches/{id}` | — | Get batch details |
| `GET` | `/api/v1/batches/{id}/roster` | — | Get batch details, `averageAttendancePercentage`, and `enrolments` list |
| `POST` | `/api/v1/batches` | — | Schedule a new batch (`201 Created`) |
| `PUT` | `/api/v1/batches/{id}` | — | Update batch details |
| `DELETE` | `/api/v1/batches/{id}` | — | Delete batch (`409` if enrolments exist) |

**Business Rules:**
- Referenced `courseId` and `trainerId` must both be active.
- `endDate` must be on or after `startDate`.
- `maxCapacity` must be greater than `0`.

**Request (`POST` / `PUT`):**
```json
{
  "batchCode": "BTH-2026-01",
  "batchName": "Morning Java Full-Stack Cohort",
  "courseId": 1,
  "trainerId": 1,
  "startDate": "2026-12-01",
  "endDate": "2027-02-20",
  "scheduleTimings": "Mon-Fri 09:00 AM - 12:00 PM",
  "mode": "ONLINE",
  "maxCapacity": 20,
  "status": "UPCOMING"
}
```

---

## 6. Enrolments (`/api/v1/enrolments`)

| Method | Endpoint | Query Params | Description |
|---|---|---|---|
| `GET` | `/api/v1/enrolments` | `batchId`, `studentId` | List enrolments with optional batch or student filter |
| `GET` | `/api/v1/enrolments/{id}` | — | Get enrolment by ID |
| `POST` | `/api/v1/enrolments` | — | Enrol a student into a batch (`201 Created`) |
| `PUT` | `/api/v1/enrolments/{id}` | — | Update enrolment record |
| `PATCH` | `/api/v1/enrolments/{id}/attendance` | — | Update `attendancePercentage` (`0.00`–`100.00`) and `remarks` |
| `DELETE` | `/api/v1/enrolments/{id}` | — | Delete enrolment |

**Business Rules:**
- Cannot enrol `INACTIVE` or `DROPPED` students.
- Cannot enrol into `CANCELLED` or `COMPLETED` batches.
- Duplicate `(studentId, batchId)` pairs return `409 Conflict`.
- Active enrolments cannot exceed batch `maxCapacity` (`409 Conflict`).

**Attendance Patch Request (`PATCH /api/v1/enrolments/{id}/attendance`):**
```json
{
  "attendancePercentage": 92.50,
  "remarks": "Consistent participation"
}
```
