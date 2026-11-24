package com.institute.management.service;

import com.institute.management.dto.request.CourseRequest;
import com.institute.management.dto.response.CourseResponse;
import com.institute.management.entity.Course;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private BatchRepository batchRepository;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, batchRepository, new EntityMapper());
    }

    @Test
    void createCourseShouldPersistAndReturnResponse() {
        CourseRequest request = new CourseRequest(
            "CRS-101", "Java & Spring Boot", "Backend track", 12, new BigDecimal("40000.00"), true
        );
        when(courseRepository.existsByCourseCode("CRS-101")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        CourseResponse response = courseService.createCourse(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.courseCode()).isEqualTo("CRS-101");
        assertThat(response.active()).isTrue();
    }

    @Test
    void createCourseShouldRejectDuplicateCode() {
        CourseRequest request = new CourseRequest(
            "CRS-101", "Java", null, 8, new BigDecimal("25000.00"), true
        );
        when(courseRepository.existsByCourseCode("CRS-101")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> courseService.createCourse(request));
        verify(courseRepository, never()).save(any());
    }

    @Test
    void deleteCourseShouldThrowConflictWhenReferencedByBatches() {
        Course existing = Course.builder().id(10L).courseCode("CRS-10").title("Java").build();
        when(courseRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(batchRepository.existsByCourseId(10L)).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> courseService.deleteCourse(10L));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(courseRepository, never()).delete(any());
    }

    @Test
    void getCourseByIdShouldThrowNotFoundWhenMissing() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(99L));
    }
}
