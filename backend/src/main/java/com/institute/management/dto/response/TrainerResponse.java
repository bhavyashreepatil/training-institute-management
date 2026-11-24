package com.institute.management.dto.response;

import java.time.LocalDateTime;

public record TrainerResponse(
    Long id,
    String employeeCode,
    String fullName,
    String email,
    String phone,
    String specialization,
    Integer experienceYears,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
