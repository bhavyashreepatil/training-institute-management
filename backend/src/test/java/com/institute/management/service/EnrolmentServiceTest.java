package com.institute.management.service;

import com.institute.management.dto.request.AttendanceUpdateRequest;
import com.institute.management.dto.request.EnrolmentRequest;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Enrolment;
import com.institute.management.entity.Student;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrolmentServiceTest {

    @Mock
    private EnrolmentRepository enrolmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BatchRepository batchRepository;

    private EnrolmentService enrolmentService;

    private Student activeStudent;
    private Batch ongoingBatch;

    @BeforeEach
    void setUp() {
        enrolmentService = new EnrolmentService(
            enrolmentRepository, studentRepository, batchRepository, new EntityMapper()
        );
        Course course = Course.builder().id(1L).courseCode("CRS-1").title("Java").active(true).build();
        Trainer trainer = Trainer.builder().id(1L).employeeCode("TRN-1").fullName("Aarav").active(true).build();
        activeStudent = Student.builder()
            .id(1L).studentCode("STU-01").fullName("Ananya").email("ananya@student.edu")
            .phone("9811110001").status(StudentStatus.ACTIVE)
            .build();
        ongoingBatch = Batch.builder()
            .id(1L).batchCode("BCH-01").batchName("Java Cohort").course(course).trainer(trainer)
            .startDate(LocalDate.of(2026, 11, 1)).endDate(LocalDate.of(2027, 1, 30))
            .scheduleTimings("09:00 - 12:00").mode(BatchMode.HYBRID).maxCapacity(5).status(BatchStatus.ONGOING)
            .build();
    }

    @Test
    void createEnrolmentShouldRejectWhenBatchCapacityExceeded() {
        EnrolmentRequest request = new EnrolmentRequest(1L, 1L, null, null, null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        when(batchRepository.findByIdWithCourseAndTrainer(1L)).thenReturn(Optional.of(ongoingBatch));
        when(enrolmentRepository.existsByStudentIdAndBatchId(1L, 1L)).thenReturn(false);
        when(enrolmentRepository.countByBatchIdAndStatus(1L, EnrolmentStatus.ENROLLED)).thenReturn(5L);

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> enrolmentService.createEnrolment(request)
        );
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("maximum capacity");
        verify(enrolmentRepository, never()).save(any());
    }

    @Test
    void createEnrolmentShouldRejectInactiveOrDroppedStudent() {
        Student inactiveStudent = Student.builder()
            .id(2L).studentCode("STU-02").fullName("Pooja").status(StudentStatus.INACTIVE).build();
        EnrolmentRequest request = new EnrolmentRequest(2L, 1L, null, null, null, null);
        when(studentRepository.findById(2L)).thenReturn(Optional.of(inactiveStudent));
        when(batchRepository.findByIdWithCourseAndTrainer(1L)).thenReturn(Optional.of(ongoingBatch));

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> enrolmentService.createEnrolment(request)
        );
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("INACTIVE");
    }

    @Test
    void createEnrolmentShouldRejectCancelledOrCompletedBatch() {
        ongoingBatch.setStatus(BatchStatus.CANCELLED);
        EnrolmentRequest request = new EnrolmentRequest(1L, 1L, null, null, null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        when(batchRepository.findByIdWithCourseAndTrainer(1L)).thenReturn(Optional.of(ongoingBatch));

        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> enrolmentService.createEnrolment(request)
        );
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("CANCELLED");
    }

    @Test
    void createEnrolmentShouldRejectDuplicateStudentBatchEnrolment() {
        EnrolmentRequest request = new EnrolmentRequest(1L, 1L, null, null, null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        when(batchRepository.findByIdWithCourseAndTrainer(1L)).thenReturn(Optional.of(ongoingBatch));
        when(enrolmentRepository.existsByStudentIdAndBatchId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> enrolmentService.createEnrolment(request));
        verify(enrolmentRepository, never()).save(any());
    }

    @Test
    void updateAttendanceShouldRejectOutOfRangePercentage() {
        Enrolment existing = Enrolment.builder()
            .id(10L).student(activeStudent).batch(ongoingBatch).enrolmentDate(LocalDate.now())
            .status(EnrolmentStatus.ENROLLED).attendancePercentage(new BigDecimal("85.00"))
            .build();
        when(enrolmentRepository.findById(10L)).thenReturn(Optional.of(existing));

        AttendanceUpdateRequest invalid = new AttendanceUpdateRequest(new BigDecimal("105.00"), "Invalid");
        BusinessRuleException ex = assertThrows(
            BusinessRuleException.class,
            () -> enrolmentService.updateAttendance(10L, invalid)
        );
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createEnrolmentShouldSucceedWhenSeatsAvailable() {
        EnrolmentRequest request = new EnrolmentRequest(
            1L, 1L, LocalDate.of(2026, 11, 1), EnrolmentStatus.ENROLLED, new BigDecimal("92.5"), "On track"
        );
        when(studentRepository.findById(1L)).thenReturn(Optional.of(activeStudent));
        when(batchRepository.findByIdWithCourseAndTrainer(1L)).thenReturn(Optional.of(ongoingBatch));
        when(enrolmentRepository.existsByStudentIdAndBatchId(1L, 1L)).thenReturn(false);
        when(enrolmentRepository.countByBatchIdAndStatus(1L, EnrolmentStatus.ENROLLED)).thenReturn(3L);
        when(enrolmentRepository.save(any(Enrolment.class))).thenAnswer(inv -> {
            Enrolment saved = inv.getArgument(0);
            saved.setId(7L);
            return saved;
        });

        EnrolmentResponse response = enrolmentService.createEnrolment(request);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.attendancePercentage()).isEqualByComparingTo(new BigDecimal("92.50"));
    }
}
