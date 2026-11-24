package com.institute.management.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
    long totalCourses,
    long activeCourses,
    long totalTrainers,
    long activeTrainers,
    long totalStudents,
    long activeStudents,
    long totalBatches,
    long activeBatches,
    long totalEnrolments,
    long activeEnrolments,
    long totalSeatCapacity,
    long totalEnrolledSeats,
    double seatUtilizationPercentage,
    BigDecimal averageAttendancePercentage,
    List<BatchResponse> activeBatchList
) {
}
