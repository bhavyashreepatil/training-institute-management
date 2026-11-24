-- =============================================================================
-- Training Institute Course and Batch Management System
-- Sample Seed Data (MySQL 8.x)
-- =============================================================================

USE institute_db;

-- -----------------------------------------------------------------------------
-- 1. Courses
-- -----------------------------------------------------------------------------
INSERT INTO courses (id, course_code, title, description, duration_weeks, fee, active) VALUES
(1, 'CRS-JAVA-101', 'Full-Stack Java & Spring Boot', 'Comprehensive enterprise backend development with Java 21, Spring Boot 3.5, JPA, and REST APIs.', 16, 45000.00, TRUE),
(2, 'CRS-ANG-102', 'Modern Angular 20 & TypeScript', 'Standalone components, reactive forms, RxJS state patterns, and Bootstrap UI engineering.', 12, 35000.00, TRUE),
(3, 'CRS-DATA-103', 'Data Engineering with Python & SQL', 'ETL pipelines, relational data modeling, query optimization, and analytical SQL.', 14, 42000.00, TRUE),
(4, 'CRS-CLD-104', 'Cloud Native & DevOps Foundations', 'Docker, Kubernetes, CI/CD pipelines, and cloud observability best practices.', 10, 38000.00, TRUE),
(5, 'CRS-QA-105', 'Automated Software Testing & QA', 'JUnit 5, Mockito, API testing, end-to-end automation, and quality gates.', 8, 28000.00, TRUE),
(6, 'CRS-LEG-106', 'Legacy Struts Migration Workshop', 'Archived legacy workshop kept inactive for historical batch records.', 4, 15000.00, FALSE);

-- -----------------------------------------------------------------------------
-- 2. Trainers
-- -----------------------------------------------------------------------------
INSERT INTO trainers (id, employee_code, full_name, email, phone, specialization, experience_years, active) VALUES
(1, 'TRN-001', 'Aarav Mehta', 'aarav.mehta@institute.edu', '+919876543210', 'Java & Spring Boot Architecture', 11, TRUE),
(2, 'TRN-002', 'Priya Nair', 'priya.nair@institute.edu', '+919876543211', 'Angular & Frontend Engineering', 8, TRUE),
(3, 'TRN-003', 'Rohan Kulkarni', 'rohan.kulkarni@institute.edu', '+919876543212', 'Database Systems & Data Engineering', 9, TRUE),
(4, 'TRN-004', 'Sneha Iyer', 'sneha.iyer@institute.edu', '+919876543213', 'DevOps & Cloud Infrastructure', 7, TRUE),
(5, 'TRN-005', 'Vikram Rao', 'vikram.rao@institute.edu', '+919876543214', 'Legacy Enterprise Systems', 14, FALSE);

-- -----------------------------------------------------------------------------
-- 3. Students
-- -----------------------------------------------------------------------------
INSERT INTO students (id, student_code, full_name, email, phone, date_of_birth, status) VALUES
(1, 'STU-2026-001', 'Ananya Sharma', 'ananya.sharma@student.edu', '+919811110001', '2001-04-12', 'ACTIVE'),
(2, 'STU-2026-002', 'Karthik Verma', 'karthik.verma@student.edu', '+919811110002', '2000-09-23', 'ACTIVE'),
(3, 'STU-2026-003', 'Meera Joshi', 'meera.joshi@student.edu', '+919811110003', '2002-01-15', 'ACTIVE'),
(4, 'STU-2026-004', 'Siddharth Menon', 'siddharth.menon@student.edu', '+919811110004', '1999-11-30', 'ACTIVE'),
(5, 'STU-2026-005', 'Divya Reddy', 'divya.reddy@student.edu', '+919811110005', '2001-07-08', 'ACTIVE'),
(6, 'STU-2026-006', 'Aditya Kapoor', 'aditya.kapoor@student.edu', '+919811110006', '2000-03-19', 'ACTIVE'),
(7, 'STU-2026-007', 'Neha Gupta', 'neha.gupta@student.edu', '+919811110007', '2001-12-02', 'ACTIVE'),
(8, 'STU-2026-008', 'Rahul Deshmukh', 'rahul.deshmukh@student.edu', '+919811110008', '1998-06-25', 'GRADUATED'),
(9, 'STU-2026-009', 'Pooja Bhat', 'pooja.bhat@student.edu', '+919811110009', '2002-05-14', 'INACTIVE'),
(10, 'STU-2026-010', 'Arjun Malhotra', 'arjun.malhotra@student.edu', '+919811110010', '2001-08-29', 'DROPPED');

-- -----------------------------------------------------------------------------
-- 4. Batches
-- -----------------------------------------------------------------------------
INSERT INTO batches (id, batch_code, batch_name, course_id, trainer_id, start_date, end_date, schedule_timings, mode, max_capacity, status) VALUES
(1, 'BCH-2026-01', 'Java Full-Stack Morning Cohort', 1, 1, '2026-10-01', '2027-01-20', 'Mon-Fri 09:00 AM - 12:00 PM', 'HYBRID', 5, 'ONGOING'),
(2, 'BCH-2026-02', 'Angular 20 Evening Cohort', 2, 2, '2026-10-15', '2027-01-10', 'Mon-Fri 06:30 PM - 08:30 PM', 'ONLINE', 15, 'ONGOING'),
(3, 'BCH-2026-03', 'Data Engineering Weekend Batch', 3, 3, '2026-12-05', '2027-03-15', 'Sat-Sun 10:00 AM - 02:00 PM', 'OFFLINE', 20, 'UPCOMING'),
(4, 'BCH-2026-04', 'Cloud & DevOps Fast-Track', 4, 4, '2026-12-10', '2027-02-18', 'Tue-Thu 07:00 PM - 09:30 PM', 'ONLINE', 12, 'UPCOMING'),
(5, 'BCH-2026-05', 'QA Automation Summer Cohort', 5, 1, '2026-07-01', '2026-08-28', 'Mon-Fri 02:00 PM - 04:30 PM', 'OFFLINE', 18, 'COMPLETED'),
(6, 'BCH-2026-06', 'Cloud Weekend Pilot (Cancelled)', 4, 4, '2026-09-01', '2026-11-10', 'Sat-Sun 03:00 PM - 06:00 PM', 'ONLINE', 10, 'CANCELLED');

-- -----------------------------------------------------------------------------
-- 5. Enrolments
-- -----------------------------------------------------------------------------
INSERT INTO enrolments (id, student_id, batch_id, enrolment_date, status, attendance_percentage, remarks) VALUES
(1, 1, 1, '2026-09-25', 'ENROLLED', 92.50, 'Strong performance in Spring Data JPA labs'),
(2, 2, 1, '2026-09-26', 'ENROLLED', 88.00, 'Consistent attendance'),
(3, 3, 1, '2026-09-27', 'ENROLLED', 95.00, 'Top lab contributor'),
(4, 4, 1, '2026-09-28', 'ENROLLED', 81.25, 'Needs extra practice on unit testing'),
(5, 1, 2, '2026-10-10', 'ENROLLED', 90.00, 'Enrolled concurrently in evening Angular track'),
(6, 5, 2, '2026-10-11', 'ENROLLED', 86.50, 'Active participant in component design reviews'),
(7, 6, 2, '2026-10-12', 'ENROLLED', 79.00, 'Improving reactive forms mastery'),
(8, 7, 3, '2026-11-20', 'ENROLLED', 0.00, 'Registered for upcoming weekend cohort'),
(9, 2, 3, '2026-11-21', 'ENROLLED', 0.00, 'Registered for upcoming weekend cohort'),
(10, 3, 4, '2026-11-22', 'ENROLLED', 0.00, 'Registered for upcoming DevOps batch'),
(11, 8, 5, '2026-06-25', 'COMPLETED', 94.00, 'Graduated with distinction'),
(12, 10, 2, '2026-10-12', 'DROPPED', 42.50, 'Withdrew due to schedule conflict');
