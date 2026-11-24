package com.institute.management.repository;

import com.institute.management.entity.Student;
import com.institute.management.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByStudentCode(String studentCode);

    boolean existsByStudentCodeAndIdNot(String studentCode, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Student> findByStudentCode(String studentCode);

    List<Student> findByStatusOrderByFullNameAsc(StudentStatus status);

    long countByStatus(StudentStatus status);

    List<Student> findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String fullName,
        String studentCode,
        String email
    );
}
