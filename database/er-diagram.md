# Entity-Relationship Diagram

## Overview

The system uses five persistent InnoDB tables in `institute_db`: `courses`, `trainers`, `students`, `batches`, and `enrolments`.

## ER Diagram

```mermaid
erDiagram
    COURSES ||--o{ BATCHES : "offered as"
    TRAINERS ||--o{ BATCHES : "instructs"
    BATCHES ||--o{ ENROLMENTS : "contains"
    STUDENTS ||--o{ ENROLMENTS : "registers in"

    COURSES {
        bigint id PK
        varchar course_code UK
        varchar title
        text description
        int duration_weeks
        decimal fee
        boolean active
        datetime created_at
        datetime updated_at
    }

    TRAINERS {
        bigint id PK
        varchar employee_code UK
        varchar full_name
        varchar email UK
        varchar phone
        varchar specialization
        int experience_years
        boolean active
        datetime created_at
        datetime updated_at
    }

    STUDENTS {
        bigint id PK
        varchar student_code UK
        varchar full_name
        varchar email UK
        varchar phone
        date date_of_birth
        enum status
        datetime created_at
        datetime updated_at
    }

    BATCHES {
        bigint id PK
        varchar batch_code UK
        varchar batch_name
        bigint course_id FK
        bigint trainer_id FK
        date start_date
        date end_date
        varchar schedule_timings
        enum mode
        int max_capacity
        enum status
        datetime created_at
        datetime updated_at
    }

    ENROLMENTS {
        bigint id PK
        bigint student_id FK
        bigint batch_id FK
        date enrolment_date
        enum status
        decimal attendance_percentage
        varchar remarks
        datetime created_at
        datetime updated_at
    }
```

## Key Constraints

- **Unique Business Keys:** `courses.course_code`, `trainers.employee_code`, `trainers.email`, `students.student_code`, `students.email`, `batches.batch_code`.
- **Composite Unique Key:** `enrolments(student_id, batch_id)` named `uk_student_batch` prevents duplicate enrolment of the same student in the same batch.
- **Referential Integrity:** All foreign keys use `ON DELETE RESTRICT ON UPDATE CASCADE` so parent records with dependent batches or enrolments cannot be removed silently.
- **Check Constraints:**
  - `courses.duration_weeks > 0` and `courses.fee >= 0.00`
  - `trainers.experience_years >= 0`
  - `batches.end_date >= batches.start_date` and `batches.max_capacity > 0`
  - `enrolments.attendance_percentage` between `0.00` and `100.00`
