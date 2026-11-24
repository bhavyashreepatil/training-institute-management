package com.institute.management.dto.response;

import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EnrolmentResponse(
    Long id,
    Long studentId,
    String studentCode,
    String studentName,
    String studentEmail,
    StudentStatus studentStatus,
    Long batchId,
    String batchCode,
    String batchName,
    String courseTitle,
    LocalDate enrolmentDate,
    EnrolmentStatus status,
    BigDecimal attendancePercentage,
    String remarks,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
