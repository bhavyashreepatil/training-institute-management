package com.institute.management.dto.request;

import com.institute.management.enums.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRequest(
    @NotBlank(message = "Student code is required")
    @Size(max = 20, message = "Student code must not exceed 20 characters")
    String studentCode,

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    String fullName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    String email,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must contain 10 to 15 digits")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    String phone,

    LocalDate dateOfBirth,

    StudentStatus status
) {
}
