package com.institute.management.repository;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class BatchRepositoryTest {

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private Course course;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        course = courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-BAT-101")
            .title("Spring Boot")
            .durationWeeks(12)
            .fee(new BigDecimal("40000.00"))
            .active(true)
            .build());
        trainer = trainerRepository.saveAndFlush(Trainer.builder()
            .employeeCode("TRN-BAT-01")
            .fullName("Aarav Mehta")
            .email("aarav.batch@institute.edu")
            .phone("9876543210")
            .specialization("Java")
            .experienceYears(10)
            .active(true)
            .build());
    }

    @Test
    void shouldRejectDuplicateBatchCode() {
        batchRepository.saveAndFlush(Batch.builder()
            .batchCode("BCH-001")
            .batchName("Morning Cohort")
            .course(course)
            .trainer(trainer)
            .startDate(LocalDate.of(2026, 12, 1))
            .endDate(LocalDate.of(2027, 2, 1))
            .scheduleTimings("09:00 - 12:00")
            .mode(BatchMode.HYBRID)
            .maxCapacity(20)
            .status(BatchStatus.UPCOMING)
            .build());

        Batch duplicate = Batch.builder()
            .batchCode("BCH-001")
            .batchName("Evening Cohort")
            .course(course)
            .trainer(trainer)
            .startDate(LocalDate.of(2026, 12, 5))
            .endDate(LocalDate.of(2027, 2, 5))
            .scheduleTimings("18:00 - 20:00")
            .mode(BatchMode.ONLINE)
            .maxCapacity(15)
            .status(BatchStatus.UPCOMING)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> batchRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldFetchBatchesWithCourseTrainerAndFilterByStatus() {
        Batch ongoing = batchRepository.saveAndFlush(Batch.builder()
            .batchCode("BCH-002")
            .batchName("Ongoing Cohort")
            .course(course)
            .trainer(trainer)
            .startDate(LocalDate.of(2026, 11, 1))
            .endDate(LocalDate.of(2027, 1, 15))
            .scheduleTimings("10:00 - 13:00")
            .mode(BatchMode.OFFLINE)
            .maxCapacity(25)
            .status(BatchStatus.ONGOING)
            .build());

        batchRepository.saveAndFlush(Batch.builder()
            .batchCode("BCH-003")
            .batchName("Completed Cohort")
            .course(course)
            .trainer(trainer)
            .startDate(LocalDate.of(2026, 7, 1))
            .endDate(LocalDate.of(2026, 9, 1))
            .scheduleTimings("14:00 - 16:00")
            .mode(BatchMode.ONLINE)
            .maxCapacity(18)
            .status(BatchStatus.COMPLETED)
            .build());

        assertThat(batchRepository.existsByCourseId(course.getId())).isTrue();
        assertThat(batchRepository.existsByTrainerId(trainer.getId())).isTrue();
        assertThat(batchRepository.countByStatusIn(List.of(BatchStatus.UPCOMING, BatchStatus.ONGOING))).isEqualTo(1L);
        assertThat(batchRepository.findByIdWithCourseAndTrainer(ongoing.getId()))
            .isPresent()
            .get()
            .satisfies(found -> {
                assertThat(found.getCourse().getCourseCode()).isEqualTo("CRS-BAT-101");
                assertThat(found.getTrainer().getEmployeeCode()).isEqualTo("TRN-BAT-01");
            });
    }
}
