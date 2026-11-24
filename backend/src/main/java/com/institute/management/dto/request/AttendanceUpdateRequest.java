package com.institute.management.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AttendanceUpdateRequest(
    @NotNull(message = "Attendance percentage is required")
    @DecimalMin(value = "0.00", message = "Attendance percentage must be at least 0.00")
    @DecimalMax(value = "100.00", message = "Attendance percentage must not exceed 100.00")
    BigDecimal attendancePercentage,

    @Size(max = 255, message = "Remarks must not exceed 255 characters")
    String remarks
) {
}
