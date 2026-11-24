package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.request.BatchRequest;
import com.institute.management.dto.response.BatchResponse;
import com.institute.management.dto.response.BatchRosterResponse;
import com.institute.management.enums.BatchMode;
import com.institute.management.enums.BatchStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.service.BatchService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BatchController.class)
@Import(GlobalExceptionHandler.class)
class BatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BatchService batchService;

    @Test
    void getBatchRosterShouldReturn200() throws Exception {
        BatchResponse batch = new BatchResponse(
            1L, "BCH-01", "Java Cohort", 1L, "CRS-101", "Java",
            1L, "Aarav", "TRN-01", LocalDate.of(2026, 11, 1), LocalDate.of(2027, 1, 30),
            "09:00 - 12:00", BatchMode.HYBRID, 20, 5L, 15L, BatchStatus.ONGOING,
            LocalDateTime.now(), LocalDateTime.now()
        );
        BatchRosterResponse roster = new BatchRosterResponse(batch, new BigDecimal("91.50"), List.of());
        when(batchService.getBatchRoster(1L)).thenReturn(roster);

        mockMvc.perform(get("/api/v1/batches/1/roster"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.batch.batchCode").value("BCH-01"))
            .andExpect(jsonPath("$.averageAttendancePercentage").value(91.5));
    }

    @Test
    void createBatchShouldReturn400WhenDateRangeInvalid() throws Exception {
        BatchRequest request = new BatchRequest(
            "BCH-01", "Java Cohort", 1L, 1L,
            LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 1),
            "09:00 - 12:00", BatchMode.ONLINE, 15, BatchStatus.UPCOMING
        );
        when(batchService.createBatch(any(BatchRequest.class)))
            .thenThrow(new BusinessRuleException("Batch end date cannot be before start date", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/api/v1/batches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
