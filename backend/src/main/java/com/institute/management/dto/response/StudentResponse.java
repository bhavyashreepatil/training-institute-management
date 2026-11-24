package com.institute.management.dto.response;

import com.institute.management.enums.StudentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentResponse(
    Long id,
    String studentCode,
    String fullName,
    String email,
    String phone,
    LocalDate dateOfBirth,
    StudentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
