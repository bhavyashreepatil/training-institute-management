package com.institute.management.service;

import com.institute.management.dto.request.BatchRequest;
import com.institute.management.dto.response.BatchResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.CourseRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.TrainerRepository;
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
class BatchServiceTest {

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    private BatchService batchService;

    private Course activeCourse;
    private Trainer activeTrainer;

    @BeforeEach
    void setUp() {
        batchService = new BatchService(
            batchRepository, courseRepository, trainerRepository, enrolmentRepository, new EntityMapper()
        );
        activeCourse = Course.builder()
            .id(1L).courseCode("CRS-101").title("Java").durationWeeks(12).fee(new BigDecimal("40000.00")).active(true)
            .build();
        activeTrainer = Trainer.builder()
            .id(1L).employeeCode("TRN-01").fullName("Aarav").email("a@inst.edu").phone("9876543210")
            .specialization("Java").experienceYears(10).active(true)
            .build();
    }

    @Test
    void createBatchShouldRejectInvalidDateRange() {
        BatchRequest request = new BatchRequest(
            "BCH-01",
            "Morning Cohort",
            1L,
            1L,
            LocalDate.of(2026, 12, 10),
            LocalDate.of(2026, 12, 1),
            "09:00 - 12:00",
            BatchMode.HYBRID,
            20,
            BatchStatus.UPCOMING
        );
        when(batchRepository.existsByBatchCode("BCH-01")).thenReturn(false);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> batchService.createBatch(request));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ex.getMessage()).contains("end date");
        verify(batchRepository, never()).save(any());
    }

    @Test
    void createBatchShouldRejectInactiveCourse() {
        Course inactiveCourse = Course.builder()
            .id(2L).courseCode("CRS-OLD").title("Legacy").durationWeeks(4).fee(BigDecimal.TEN).active(false)
            .build();
        BatchRequest request = new BatchRequest(
            "BCH-02", "Legacy Batch", 2L, 1L,
            LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 30),
            "10:00 - 12:00", BatchMode.ONLINE, 15, BatchStatus.UPCOMING
        );
        when(batchRepository.existsByBatchCode("BCH-02")).thenReturn(false);
        when(courseRepository.findById(2L)).thenReturn(Optional.of(inactiveCourse));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(activeTrainer));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> batchService.createBatch(request));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("inactive course");
    }

    @Test
    void createBatchShouldRejectInactiveTrainer() {
        Trainer inactiveTrainer = Trainer.builder()
            .id(2L).employeeCode("TRN-OLD").fullName("Vikram").active(false).build();
        BatchRequest request = new BatchRequest(
            "BCH-03", "Cohort", 1L, 2L,
            LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 30),
            "10:00 - 12:00", BatchMode.ONLINE, 15, BatchStatus.UPCOMING
        );
        when(batchRepository.existsByBatchCode("BCH-03")).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse));
        when(trainerRepository.findById(2L)).thenReturn(Optional.of(inactiveTrainer));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> batchService.createBatch(request));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ex.getMessage()).contains("inactive trainer");
    }

    @Test
    void createBatchShouldThrowNotFoundForMissingCourse() {
        BatchRequest request = new BatchRequest(
            "BCH-04", "Cohort", 999L, 1L,
            LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 30),
            "10:00 - 12:00", BatchMode.ONLINE, 15, BatchStatus.UPCOMING
        );
        when(batchRepository.existsByBatchCode("BCH-04")).thenReturn(false);
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> batchService.createBatch(request));
    }

    @Test
    void updateBatchShouldRejectCapacityLowerThanCurrentEnrolledCount() {
        Batch existing = Batch.builder()
            .id(10L).batchCode("BCH-10").batchName("Cohort 10").course(activeCourse).trainer(activeTrainer)
            .startDate(LocalDate.of(2026, 12, 1)).endDate(LocalDate.of(2027, 2, 1))
            .scheduleTimings("09:00 - 12:00").mode(BatchMode.HYBRID).maxCapacity(10).status(BatchStatus.ONGOING)
            .build();
        BatchRequest update = new BatchRequest(
            "BCH-10", "Cohort 10", 1L, 1L,
            LocalDate.of(2026, 12, 1), LocalDate.of(2027, 2, 1),
            "09:00 - 12:00", BatchMode.HYBRID, 3, BatchStatus.ONGOING
        );
        when(batchRepository.findByIdWithCourseAndTrainer(10L)).thenReturn(Optional.of(existing));
        when(batchRepository.existsByBatchCodeAndIdNot("BCH-10", 10L)).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(activeTrainer));
        when(enrolmentRepository.countByBatchIdAndStatus(10L, EnrolmentStatus.ENROLLED)).thenReturn(5L);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> batchService.updateBatch(10L, update));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createBatchShouldSucceedWithValidInputs() {
        BatchRequest request = new BatchRequest(
            "BCH-05", "Java Cohort", 1L, 1L,
            LocalDate.of(2026, 12, 1), LocalDate.of(2027, 2, 1),
            "09:00 - 12:00", BatchMode.HYBRID, 25, null
        );
        when(batchRepository.existsByBatchCode("BCH-05")).thenReturn(false);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(activeCourse));
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(activeTrainer));
        when(batchRepository.save(any(Batch.class))).thenAnswer(inv -> {
            Batch saved = inv.getArgument(0);
            saved.setId(5L);
            return saved;
        });

        BatchResponse response = batchService.createBatch(request);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.status()).isEqualTo(BatchStatus.UPCOMING);
        assertThat(response.availableSeats()).isEqualTo(25L);
    }
}
