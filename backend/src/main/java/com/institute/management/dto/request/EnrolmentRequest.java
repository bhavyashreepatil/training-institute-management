package com.institute.management.dto.request;

import com.institute.management.enums.EnrolmentStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EnrolmentRequest(
    @NotNull(message = "Student ID is required")
    Long studentId,

    @NotNull(message = "Batch ID is required")
    Long batchId,

    LocalDate enrolmentDate,

    EnrolmentStatus status,

    @DecimalMin(value = "0.00", message = "Attendance percentage must be at least 0.00")
    @DecimalMax(value = "100.00", message = "Attendance percentage must not exceed 100.00")
    BigDecimal attendancePercentage,

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    String remarks
) {
}
