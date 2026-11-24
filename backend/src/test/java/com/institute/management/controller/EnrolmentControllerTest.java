package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.request.AttendanceUpdateRequest;
import com.institute.management.dto.request.EnrolmentRequest;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.service.EnrolmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrolmentController.class)
@Import(GlobalExceptionHandler.class)
class EnrolmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EnrolmentService enrolmentService;

    @Test
    void createEnrolmentShouldReturn409WhenCapacityExceeded() throws Exception {
        EnrolmentRequest request = new EnrolmentRequest(1L, 1L, null, null, null, null);
        when(enrolmentService.createEnrolment(any(EnrolmentRequest.class)))
            .thenThrow(new BusinessRuleException("Batch 'BCH-01' has reached its maximum capacity", HttpStatus.CONFLICT));

        mockMvc.perform(post("/api/v1/enrolments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void updateAttendanceShouldReturn200WhenValid() throws Exception {
        AttendanceUpdateRequest request = new AttendanceUpdateRequest(new BigDecimal("93.50"), "Great progress");
        EnrolmentResponse updated = new EnrolmentResponse(
            1L, 1L, "STU-01", "Ananya", "ananya@student.edu", StudentStatus.ACTIVE,
            1L, "BCH-01", "Java Cohort", "Java", LocalDate.of(2026, 11, 1),
            EnrolmentStatus.ENROLLED, new BigDecimal("93.50"), "Great progress",
            LocalDateTime.now(), LocalDateTime.now()
        );
        when(enrolmentService.updateAttendance(eq(1L), any(AttendanceUpdateRequest.class))).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/enrolments/1/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attendancePercentage").value(93.5));
    }

    @Test
    void updateAttendanceShouldReturn400WhenOver100() throws Exception {
        AttendanceUpdateRequest invalid = new AttendanceUpdateRequest(new BigDecimal("120.00"), null);

        mockMvc.perform(patch("/api/v1/enrolments/1/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.attendancePercentage").exists());
    }
}
