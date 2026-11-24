package com.institute.management.repository;

import com.institute.management.entity.Batch;
import com.institute.management.enums.BatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    boolean existsByBatchCode(String batchCode);

    boolean existsByBatchCodeAndIdNot(String batchCode, Long id);

    boolean existsByCourseId(Long courseId);

    boolean existsByTrainerId(Long trainerId);

    List<Batch> findByCourseId(Long courseId);

    List<Batch> findByTrainerId(Long trainerId);

    List<Batch> findByStatus(BatchStatus status);

    List<Batch> findByStatusInOrderByStartDateAsc(List<BatchStatus> statuses);

    long countByStatusIn(List<BatchStatus> statuses);

    @Query("SELECT b FROM Batch b JOIN FETCH b.course JOIN FETCH b.trainer ORDER BY b.startDate DESC")
    List<Batch> findAllWithCourseAndTrainer();

    @Query("SELECT b FROM Batch b JOIN FETCH b.course JOIN FETCH b.trainer WHERE b.id = :id")
    Optional<Batch> findByIdWithCourseAndTrainer(@Param("id") Long id);

    @Query("SELECT b FROM Batch b JOIN FETCH b.course JOIN FETCH b.trainer WHERE b.status IN :statuses ORDER BY b.startDate ASC")
    List<Batch> findByStatusInWithCourseAndTrainer(@Param("statuses") List<BatchStatus> statuses);
}
