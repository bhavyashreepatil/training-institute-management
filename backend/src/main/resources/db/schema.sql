-- Reference schema aligned with database/schema.sql (MySQL 8.x InnoDB)

CREATE TABLE IF NOT EXISTS courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NULL,
    duration_weeks INT NOT NULL,
    fee DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_courses_course_code UNIQUE (course_code),
    CONSTRAINT chk_courses_duration_positive CHECK (duration_weeks > 0),
    CONSTRAINT chk_courses_fee_non_negative CHECK (fee >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS trainers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    employee_code VARCHAR(20) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    specialization VARCHAR(120) NOT NULL,
    experience_years INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_trainers_employee_code UNIQUE (employee_code),
    CONSTRAINT uk_trainers_email UNIQUE (email),
    CONSTRAINT chk_trainers_experience_non_negative CHECK (experience_years >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_code VARCHAR(20) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    date_of_birth DATE NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'GRADUATED', 'DROPPED') NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_students_student_code UNIQUE (student_code),
    CONSTRAINT uk_students_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS batches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    batch_code VARCHAR(20) NOT NULL,
    batch_name VARCHAR(150) NOT NULL,
    course_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    schedule_timings VARCHAR(100) NOT NULL,
    mode ENUM('ONLINE', 'OFFLINE', 'HYBRID') NOT NULL,
    max_capacity INT NOT NULL,
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_batches_batch_code UNIQUE (batch_code),
    CONSTRAINT fk_batches_course FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_batches_trainer FOREIGN KEY (trainer_id) REFERENCES trainers (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_batches_date_range CHECK (end_date >= start_date),
    CONSTRAINT chk_batches_max_capacity_positive CHECK (max_capacity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS enrolments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    enrolment_date DATE NOT NULL,
    status ENUM('ENROLLED', 'COMPLETED', 'DROPPED') NOT NULL,
    attendance_percentage DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    remarks VARCHAR(255) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_student_batch UNIQUE (student_id, batch_id),
    CONSTRAINT fk_enrolments_student FOREIGN KEY (student_id) REFERENCES students (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_enrolments_batch FOREIGN KEY (batch_id) REFERENCES batches (id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_enrolments_attendance_range CHECK (attendance_percentage >= 0.00 AND attendance_percentage <= 100.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
