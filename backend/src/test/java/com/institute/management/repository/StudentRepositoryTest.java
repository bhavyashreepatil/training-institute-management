package com.institute.management.repository;

import com.institute.management.entity.Student;
import com.institute.management.enums.StudentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldRejectDuplicateStudentCodeAndEmail() {
        studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-001")
            .fullName("Ananya Sharma")
            .email("ananya@student.edu")
            .phone("9811110001")
            .dateOfBirth(LocalDate.of(2001, 4, 12))
            .status(StudentStatus.ACTIVE)
            .build());

        Student duplicateCode = Student.builder()
            .studentCode("STU-001")
            .fullName("Another Student")
            .email("another@student.edu")
            .phone("9811110002")
            .status(StudentStatus.ACTIVE)
            .build();
        assertThrows(DataIntegrityViolationException.class, () -> studentRepository.saveAndFlush(duplicateCode));

        Student duplicateEmail = Student.builder()
            .studentCode("STU-002")
            .fullName("Duplicate Email")
            .email("ananya@student.edu")
            .phone("9811110003")
            .status(StudentStatus.ACTIVE)
            .build();
        assertThrows(DataIntegrityViolationException.class, () -> studentRepository.saveAndFlush(duplicateEmail));
    }

    @Test
    void shouldQueryStudentsByStatusAndSearchTerm() {
        studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-101")
            .fullName("Meera Joshi")
            .email("meera@student.edu")
            .phone("9811110011")
            .status(StudentStatus.ACTIVE)
            .build());
        studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-102")
            .fullName("Arjun Malhotra")
            .email("arjun@student.edu")
            .phone("9811110012")
            .status(StudentStatus.DROPPED)
            .build());

        assertThat(studentRepository.countByStatus(StudentStatus.ACTIVE)).isEqualTo(1L);
        assertThat(studentRepository.findByStatusOrderByFullNameAsc(StudentStatus.ACTIVE))
            .extracting(Student::getStudentCode)
            .containsExactly("STU-101");
        assertThat(studentRepository
            .findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                "meera", "meera", "meera"))
            .hasSize(1);
    }
}
