package com.institute.management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CourseRequest(
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    String courseCode,

    @NotBlank(message = "Course title is required")
    @Size(max = 150, message = "Course title must not exceed 150 characters")
    String title,

    String description,

    @NotNull(message = "Duration in weeks is required")
    @Positive(message = "Duration must be greater than zero")
    Integer durationWeeks,

    @NotNull(message = "Course fee is required")
    @DecimalMin(value = "0.00", message = "Course fee cannot be negative")
    BigDecimal fee,

    Boolean active
) {
}
