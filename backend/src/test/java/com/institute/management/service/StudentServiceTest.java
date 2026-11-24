package com.institute.management.service;

import com.institute.management.dto.request.StudentRequest;
import com.institute.management.dto.response.StudentResponse;
import com.institute.management.entity.Student;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        studentService = new StudentService(studentRepository, enrolmentRepository, new EntityMapper());
    }

    @Test
    void createStudentShouldDefaultStatusToActiveWhenNull() {
        StudentRequest request = new StudentRequest(
            "STU-01", "Ananya Sharma", "ananya@student.edu", "9811110001", LocalDate.of(2001, 4, 12), null
        );
        when(studentRepository.existsByStudentCode("STU-01")).thenReturn(false);
        when(studentRepository.existsByEmail("ananya@student.edu")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        StudentResponse response = studentService.createStudent(request);

        assertThat(response.status()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    void createStudentShouldRejectDuplicateStudentCode() {
        StudentRequest request = new StudentRequest(
            "STU-01", "Ananya Sharma", "ananya@student.edu", "9811110001", null, StudentStatus.ACTIVE
        );
        when(studentRepository.existsByStudentCode("STU-01")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.createStudent(request));
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudentShouldThrowConflictWhenEnrolmentsExist() {
        Student existing = Student.builder().id(3L).studentCode("STU-03").fullName("Student").build();
        when(studentRepository.findById(3L)).thenReturn(Optional.of(existing));
        when(enrolmentRepository.existsByStudentId(3L)).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> studentService.deleteStudent(3L));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
}
