package com.institute.management.service;

import com.institute.management.dto.response.DashboardResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.CourseRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import com.institute.management.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    @Mock
    private BatchService batchService;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
            courseRepository,
            trainerRepository,
            studentRepository,
            batchRepository,
            enrolmentRepository,
            batchService,
            new EntityMapper()
        );
    }

    @Test
    void getSummaryShouldComputeLiveKpisAndSeatUtilization() {
        Course course = Course.builder().id(1L).courseCode("CRS-1").title("Java").active(true).build();
        Trainer trainer = Trainer.builder().id(1L).employeeCode("TRN-1").fullName("Aarav").active(true).build();
        Batch batch = Batch.builder()
            .id(10L).batchCode("BCH-10").batchName("Java Cohort").course(course).trainer(trainer)
            .startDate(LocalDate.of(2026, 11, 1)).endDate(LocalDate.of(2027, 1, 20))
            .scheduleTimings("09:00 - 12:00").mode(BatchMode.HYBRID).maxCapacity(20).status(BatchStatus.ONGOING)
            .build();

        when(courseRepository.count()).thenReturn(6L);
        when(courseRepository.countByActiveTrue()).thenReturn(5L);
        when(trainerRepository.count()).thenReturn(5L);
        when(trainerRepository.countByActiveTrue()).thenReturn(4L);
        when(studentRepository.count()).thenReturn(10L);
        when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(7L);
        when(batchRepository.count()).thenReturn(6L);
        when(batchRepository.countByStatusIn(anyList())).thenReturn(4L);
        when(enrolmentRepository.count()).thenReturn(12L);
        when(enrolmentRepository.countByStatus(EnrolmentStatus.ENROLLED)).thenReturn(10L);
        when(batchRepository.findByStatusInWithCourseAndTrainer(anyList())).thenReturn(List.of(batch));
        when(batchService.getEnrolledCountMap()).thenReturn(Map.of(10L, 15L));
        when(enrolmentRepository.findAverageAttendancePercentage()).thenReturn(new BigDecimal("88.456"));

        DashboardResponse summary = dashboardService.getSummary();

        assertThat(summary.totalCourses()).isEqualTo(6L);
        assertThat(summary.activeTrainers()).isEqualTo(4L);
        assertThat(summary.activeStudents()).isEqualTo(7L);
        assertThat(summary.activeBatches()).isEqualTo(4L);
        assertThat(summary.totalSeatCapacity()).isEqualTo(20L);
        assertThat(summary.totalEnrolledSeats()).isEqualTo(15L);
        assertThat(summary.seatUtilizationPercentage()).isEqualTo(75.0);
        assertThat(summary.averageAttendancePercentage()).isEqualByComparingTo(new BigDecimal("88.46"));
        assertThat(summary.activeBatchList()).hasSize(1);
    }
}
