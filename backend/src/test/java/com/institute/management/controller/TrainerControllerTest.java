package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.request.TrainerRequest;
import com.institute.management.dto.response.TrainerResponse;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@Import(GlobalExceptionHandler.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerService trainerService;

    @Test
    void getAllTrainersShouldReturn200() throws Exception {
        TrainerResponse trainer = new TrainerResponse(
            1L, "TRN-01", "Aarav Mehta", "aarav@institute.edu", "+919876543210",
            "Java", 10, true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(trainerService.getAllTrainers(null, null)).thenReturn(List.of(trainer));

        mockMvc.perform(get("/api/v1/trainers"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].employeeCode").value("TRN-01"));
    }

    @Test
    void createTrainerShouldReturn400OnInvalidEmailAndPhone() throws Exception {
        TrainerRequest invalid = new TrainerRequest(
            "TRN-01", "Aarav", "not-an-email", "123", "Java", 5, true
        );

        mockMvc.perform(post("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.email").exists())
            .andExpect(jsonPath("$.fieldErrors.phone").exists());
    }

    @Test
    void createTrainerShouldReturn409OnDuplicateEmployeeCode() throws Exception {
        TrainerRequest request = new TrainerRequest(
            "TRN-01", "Aarav", "aarav@institute.edu", "+919876543210", "Java", 10, true
        );
        when(trainerService.createTrainer(any(TrainerRequest.class)))
            .thenThrow(new DuplicateResourceException("Employee code already exists: TRN-01"));

        mockMvc.perform(post("/api/v1/trainers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }
}
