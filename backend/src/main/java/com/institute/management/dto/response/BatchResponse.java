package com.institute.management.dto.response;

import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BatchResponse(
    Long id,
    String batchCode,
    String batchName,
    Long courseId,
    String courseCode,
    String courseTitle,
    Long trainerId,
    String trainerName,
    String trainerEmployeeCode,
    LocalDate startDate,
    LocalDate endDate,
    String scheduleTimings,
    BatchMode mode,
    Integer maxCapacity,
    long enrolledCount,
    long availableSeats,
    BatchStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
