package com.institute.management.repository;

import com.institute.management.entity.Enrolment;
import com.institute.management.enums.EnrolmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EnrolmentRepository extends JpaRepository<Enrolment, Long> {

    boolean existsByStudentIdAndBatchId(Long studentId, Long batchId);

    boolean existsByStudentIdAndBatchIdAndIdNot(Long studentId, Long batchId, Long id);

    boolean existsByStudentId(Long studentId);

    boolean existsByBatchId(Long batchId);

    long countByBatchIdAndStatus(Long batchId, EnrolmentStatus status);

    long countByStatus(EnrolmentStatus status);

    @Query("SELECT e.batch.id, COUNT(e) FROM Enrolment e WHERE e.status = :status GROUP BY e.batch.id")
    List<Object[]> countGroupedByBatchIdAndStatus(@Param("status") EnrolmentStatus status);

    long countByBatchIdAndStatusIn(Long batchId, List<EnrolmentStatus> statuses);

    List<Enrolment> findByBatchId(Long batchId);

    List<Enrolment> findByStudentId(Long studentId);

    @Query("""
        SELECT e FROM Enrolment e
        JOIN FETCH e.student s
        JOIN FETCH e.batch b
        JOIN FETCH b.course
        JOIN FETCH b.trainer
        ORDER BY e.enrolmentDate DESC, e.id DESC
        """)
    List<Enrolment> findAllWithDetails();

    @Query("""
        SELECT e FROM Enrolment e
        JOIN FETCH e.student s
        JOIN FETCH e.batch b
        JOIN FETCH b.course
        JOIN FETCH b.trainer
        WHERE e.batch.id = :batchId
        ORDER BY s.fullName ASC
        """)
    List<Enrolment> findByBatchIdWithDetails(@Param("batchId") Long batchId);

    @Query("""
        SELECT e FROM Enrolment e
        JOIN FETCH e.student s
        JOIN FETCH e.batch b
        JOIN FETCH b.course
        JOIN FETCH b.trainer
        WHERE e.student.id = :studentId
        ORDER BY e.enrolmentDate DESC
        """)
    List<Enrolment> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    @Query("""
        SELECT COALESCE(AVG(e.attendancePercentage), 0)
        FROM Enrolment e
        WHERE e.status <> com.institute.management.enums.EnrolmentStatus.DROPPED
        """)
    BigDecimal findAverageAttendancePercentage();

    @Query("""
        SELECT COALESCE(AVG(e.attendancePercentage), 0)
        FROM Enrolment e
        WHERE e.batch.id = :batchId
          AND e.status <> com.institute.management.enums.EnrolmentStatus.DROPPED
        """)
    BigDecimal findAverageAttendanceByBatchId(@Param("batchId") Long batchId);
}
