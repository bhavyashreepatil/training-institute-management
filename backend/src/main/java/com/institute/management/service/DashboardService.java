package com.institute.management.service;

import com.institute.management.dto.response.BatchResponse;
import com.institute.management.dto.response.DashboardResponse;
import com.institute.management.entity.Batch;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.CourseRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import com.institute.management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final List<BatchStatus> ACTIVE_BATCH_STATUSES = List.of(
        BatchStatus.ONGOING,
        BatchStatus.UPCOMING
    );

    private final CourseRepository courseRepository;
    private final TrainerRepository trainerRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final BatchService batchService;
    private final EntityMapper entityMapper;

    public DashboardResponse getSummary() {
        long totalCourses = courseRepository.count();
        long activeCourses = courseRepository.countByActiveTrue();
        long totalTrainers = trainerRepository.count();
        long activeTrainers = trainerRepository.countByActiveTrue();
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.countByStatus(StudentStatus.ACTIVE);
        long totalBatches = batchRepository.count();
        long activeBatches = batchRepository.countByStatusIn(ACTIVE_BATCH_STATUSES);
        long totalEnrolments = enrolmentRepository.count();
        long activeEnrolments = enrolmentRepository.countByStatus(EnrolmentStatus.ENROLLED);

        List<Batch> upcomingAndOngoing = batchRepository.findByStatusInWithCourseAndTrainer(ACTIVE_BATCH_STATUSES);
        Map<Long, Long> enrolledCounts = batchService.getEnrolledCountMap();

        List<BatchResponse> activeBatchList = upcomingAndOngoing.stream()
            .map(batch -> entityMapper.toBatchResponse(batch, enrolledCounts.getOrDefault(batch.getId(), 0L)))
            .toList();

        long totalSeatCapacity = activeBatchList.stream()
            .mapToLong(BatchResponse::maxCapacity)
            .sum();
        long totalEnrolledSeats = activeBatchList.stream()
            .mapToLong(BatchResponse::enrolledCount)
            .sum();
        double seatUtilizationPercentage = totalSeatCapacity > 0
            ? BigDecimal.valueOf((totalEnrolledSeats * 100.0) / totalSeatCapacity)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue()
            : 0.0;

        BigDecimal avgAttendance = enrolmentRepository.findAverageAttendancePercentage();
        BigDecimal normalizedAvgAttendance = avgAttendance != null
            ? avgAttendance.setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return new DashboardResponse(
            totalCourses,
            activeCourses,
            totalTrainers,
            activeTrainers,
            totalStudents,
            activeStudents,
            totalBatches,
            activeBatches,
            totalEnrolments,
            activeEnrolments,
            totalSeatCapacity,
            totalEnrolledSeats,
            seatUtilizationPercentage,
            normalizedAvgAttendance,
            activeBatchList
        );
    }
}
