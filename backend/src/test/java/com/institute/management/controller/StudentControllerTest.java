package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.request.StudentRequest;
import com.institute.management.dto.response.StudentResponse;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
@Import(GlobalExceptionHandler.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentService studentService;

    @Test
    void getAllStudentsShouldReturn200() throws Exception {
        StudentResponse student = new StudentResponse(
            1L, "STU-01", "Ananya Sharma", "ananya@student.edu", "9811110001",
            LocalDate.of(2001, 4, 12), StudentStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );
        when(studentService.getAllStudents(null, null)).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].studentCode").value("STU-01"));
    }

    @Test
    void createStudentShouldReturn201() throws Exception {
        StudentRequest request = new StudentRequest(
            "STU-01", "Ananya Sharma", "ananya@student.edu", "9811110001",
            LocalDate.of(2001, 4, 12), StudentStatus.ACTIVE
        );
        StudentResponse created = new StudentResponse(
            1L, "STU-01", "Ananya Sharma", "ananya@student.edu", "9811110001",
            LocalDate.of(2001, 4, 12), StudentStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );
        when(studentService.createStudent(any(StudentRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deleteStudentShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/students/1"))
            .andExpect(status().isNoContent());
    }
}
