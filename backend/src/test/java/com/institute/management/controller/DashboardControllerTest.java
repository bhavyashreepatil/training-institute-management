package com.institute.management.controller;

import com.institute.management.dto.response.DashboardResponse;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(GlobalExceptionHandler.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    void getSummaryShouldReturn200WithMetrics() throws Exception {
        DashboardResponse summary = new DashboardResponse(
            6L, 5L, 5L, 4L, 10L, 7L, 6L, 4L, 12L, 9L,
            52L, 9L, 17.31, new BigDecimal("88.50"), List.of()
        );
        when(dashboardService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/v1/dashboard/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCourses").value(6))
            .andExpect(jsonPath("$.activeTrainers").value(4))
            .andExpect(jsonPath("$.activeStudents").value(7))
            .andExpect(jsonPath("$.seatUtilizationPercentage").value(17.31))
            .andExpect(jsonPath("$.averageAttendancePercentage").value(88.5));
    }
}
