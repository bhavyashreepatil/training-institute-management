package com.institute.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.institute.management.dto.request.CourseRequest;
import com.institute.management.dto.response.CourseResponse;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.GlobalExceptionHandler;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CourseController.class)
@Import(GlobalExceptionHandler.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @Test
    void getAllCoursesShouldReturnList() throws Exception {
        CourseResponse course = new CourseResponse(
            1L, "CRS-101", "Java", "Backend", 12, new BigDecimal("40000.00"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );
        when(courseService.getAllCourses(null, null)).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].courseCode").value("CRS-101"))
            .andExpect(jsonPath("$[0].title").value("Java"));
    }

    @Test
    void getCourseByIdShouldReturn404WhenMissing() throws Exception {
        when(courseService.getCourseById(99L)).thenThrow(new ResourceNotFoundException("Course", 99L));

        mockMvc.perform(get("/api/v1/courses/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Course not found with id: 99"));
    }

    @Test
    void createCourseShouldReturn400WhenValidationFails() throws Exception {
        CourseRequest invalid = new CourseRequest("", "", null, -2, new BigDecimal("-10.00"), true);

        mockMvc.perform(post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.fieldErrors.courseCode").exists())
            .andExpect(jsonPath("$.fieldErrors.durationWeeks").exists())
            .andExpect(jsonPath("$.fieldErrors.fee").exists());
    }

    @Test
    void createCourseShouldReturn201WhenValid() throws Exception {
        CourseRequest request = new CourseRequest(
            "CRS-101", "Java", "Backend", 12, new BigDecimal("40000.00"), true
        );
        CourseResponse created = new CourseResponse(
            1L, "CRS-101", "Java", "Backend", 12, new BigDecimal("40000.00"), true,
            LocalDateTime.now(), LocalDateTime.now()
        );
        when(courseService.createCourse(any(CourseRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteCourseShouldReturn409WhenReferenced() throws Exception {
        doThrow(new BusinessRuleException("Course is referenced by existing batches", HttpStatus.CONFLICT))
            .when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/v1/courses/1"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409));
    }
}
