-- Reference seed data aligned with database/sample-data.sql

INSERT IGNORE INTO courses (id, course_code, title, description, duration_weeks, fee, active) VALUES
(1, 'CRS-JAVA-101', 'Full-Stack Java & Spring Boot', 'Comprehensive enterprise backend development with Java 21, Spring Boot 3.5, JPA, and REST APIs.', 16, 45000.00, TRUE),
(2, 'CRS-ANG-102', 'Modern Angular 20 & TypeScript', 'Standalone components, reactive forms, RxJS state patterns, and Bootstrap UI engineering.', 12, 35000.00, TRUE),
(3, 'CRS-DATA-103', 'Data Engineering with Python & SQL', 'ETL pipelines, relational data modeling, query optimization, and analytical SQL.', 14, 42000.00, TRUE),
(4, 'CRS-CLD-104', 'Cloud Native & DevOps Foundations', 'Docker, Kubernetes, CI/CD pipelines, and cloud observability best practices.', 10, 38000.00, TRUE),
(5, 'CRS-QA-105', 'Automated Software Testing & QA', 'JUnit 5, Mockito, API testing, end-to-end automation, and quality gates.', 8, 28000.00, TRUE),
(6, 'CRS-LEG-106', 'Legacy Struts Migration Workshop', 'Archived legacy workshop kept inactive for historical batch records.', 4, 15000.00, FALSE);
