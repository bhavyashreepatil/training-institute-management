package com.institute.management.dto.request;

import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BatchRequest(
    @NotBlank(message = "Batch code is required")
    @Size(max = 20, message = "Batch code must not exceed 20 characters")
    String batchCode,

    @NotBlank(message = "Batch name is required")
    @Size(max = 150, message = "Batch name must not exceed 150 characters")
    String batchName,

    @NotNull(message = "Course ID is required")
    Long courseId,

    @NotNull(message = "Trainer ID is required")
    Long trainerId,

    @NotNull(message = "Start date is required")
    LocalDate startDate,

    @NotNull(message = "End date is required")
    LocalDate endDate,

    @NotBlank(message = "Schedule timings are required")
    @Size(max = 100, message = "Schedule timings must not exceed 100 characters")
    String scheduleTimings,

    @NotNull(message = "Batch mode is required")
    BatchMode mode,

    @NotNull(message = "Max capacity is required")
    @Positive(message = "Max capacity must be greater than zero")
    Integer maxCapacity,

    BatchStatus status
) {
}
