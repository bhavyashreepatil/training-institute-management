package com.institute.management.repository;

import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Enrolment;
import com.institute.management.entity.Student;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class EnrolmentRepositoryTest {

    @Autowired
    private EnrolmentRepository enrolmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private Student studentOne;
    private Student studentTwo;
    private Student studentThree;
    private Batch batch;

    @BeforeEach
    void setUp() {
        Course course = courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-ENR-101")
            .title("Full-Stack Java")
            .durationWeeks(16)
            .fee(new BigDecimal("45000.00"))
            .active(true)
            .build());
        Trainer trainer = trainerRepository.saveAndFlush(Trainer.builder()
            .employeeCode("TRN-ENR-01")
            .fullName("Priya Nair")
            .email("priya.enr@institute.edu")
            .phone("9876543219")
            .specialization("Java & Angular")
            .experienceYears(8)
            .active(true)
            .build());
        batch = batchRepository.saveAndFlush(Batch.builder()
            .batchCode("BCH-ENR-01")
            .batchName("Full-Stack Batch 1")
            .course(course)
            .trainer(trainer)
            .startDate(LocalDate.of(2026, 11, 1))
            .endDate(LocalDate.of(2027, 2, 1))
            .scheduleTimings("09:00 - 12:00")
            .mode(BatchMode.HYBRID)
            .maxCapacity(5)
            .status(BatchStatus.ONGOING)
            .build());
        studentOne = studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-ENR-01")
            .fullName("Ananya Sharma")
            .email("ananya.enr@student.edu")
            .phone("9811110101")
            .status(StudentStatus.ACTIVE)
            .build());
        studentTwo = studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-ENR-02")
            .fullName("Karthik Verma")
            .email("karthik.enr@student.edu")
            .phone("9811110102")
            .status(StudentStatus.ACTIVE)
            .build());
        studentThree = studentRepository.saveAndFlush(Student.builder()
            .studentCode("STU-ENR-03")
            .fullName("Arjun Malhotra")
            .email("arjun.enr@student.edu")
            .phone("9811110103")
            .status(StudentStatus.ACTIVE)
            .build());
    }

    @Test
    void shouldEnforceUniqueStudentBatchConstraint() {
        enrolmentRepository.saveAndFlush(Enrolment.builder()
            .student(studentOne)
            .batch(batch)
            .enrolmentDate(LocalDate.of(2026, 11, 1))
            .status(EnrolmentStatus.ENROLLED)
            .attendancePercentage(new BigDecimal("90.00"))
            .build());

        Enrolment duplicate = Enrolment.builder()
            .student(studentOne)
            .batch(batch)
            .enrolmentDate(LocalDate.of(2026, 11, 2))
            .status(EnrolmentStatus.ENROLLED)
            .attendancePercentage(new BigDecimal("85.00"))
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> enrolmentRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldCountEnrolledAndCalculateAverageAttendanceExcludingDropped() {
        enrolmentRepository.saveAndFlush(Enrolment.builder()
            .student(studentOne)
            .batch(batch)
            .enrolmentDate(LocalDate.of(2026, 11, 1))
            .status(EnrolmentStatus.ENROLLED)
            .attendancePercentage(new BigDecimal("90.00"))
            .build());
        enrolmentRepository.saveAndFlush(Enrolment.builder()
            .student(studentTwo)
            .batch(batch)
            .enrolmentDate(LocalDate.of(2026, 11, 1))
            .status(EnrolmentStatus.ENROLLED)
            .attendancePercentage(new BigDecimal("80.00"))
            .build());
        enrolmentRepository.saveAndFlush(Enrolment.builder()
            .student(studentThree)
            .batch(batch)
            .enrolmentDate(LocalDate.of(2026, 11, 1))
            .status(EnrolmentStatus.DROPPED)
            .attendancePercentage(new BigDecimal("20.00"))
            .build());

        assertThat(enrolmentRepository.existsByStudentIdAndBatchId(studentOne.getId(), batch.getId())).isTrue();
        assertThat(enrolmentRepository.countByBatchIdAndStatus(batch.getId(), EnrolmentStatus.ENROLLED)).isEqualTo(2L);
        assertThat(enrolmentRepository.findByBatchId(batch.getId())).hasSize(3);
        assertThat(enrolmentRepository.findByStudentId(studentOne.getId())).hasSize(1);
        assertThat(enrolmentRepository.findByBatchIdWithDetails(batch.getId())).hasSize(3);
        assertThat(enrolmentRepository.findByStudentIdWithDetails(studentOne.getId())).hasSize(1);
        assertThat(enrolmentRepository.findAverageAttendancePercentage())
            .isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(enrolmentRepository.findAverageAttendanceByBatchId(batch.getId()))
            .isEqualByComparingTo(new BigDecimal("85.00"));
    }
}
