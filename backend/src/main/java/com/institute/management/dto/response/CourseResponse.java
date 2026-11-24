package com.institute.management.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseResponse(
    Long id,
    String courseCode,
    String title,
    String description,
    Integer durationWeeks,
    BigDecimal fee,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
