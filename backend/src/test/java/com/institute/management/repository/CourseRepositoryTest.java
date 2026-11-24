package com.institute.management.repository;

import com.institute.management.entity.Course;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void shouldPersistCourseWithTimestampsAndDefaultActive() {
        Course course = Course.builder()
            .courseCode("CRS-TEST-101")
            .title("Spring Boot Microservices")
            .description("Distributed architecture with Spring Boot")
            .durationWeeks(10)
            .fee(new BigDecimal("32000.00"))
            .build();

        Course saved = courseRepository.saveAndFlush(course);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldRejectDuplicateCourseCode() {
        courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-DUP-001")
            .title("Java Core")
            .durationWeeks(8)
            .fee(new BigDecimal("20000.00"))
            .active(true)
            .build());

        Course duplicate = Course.builder()
            .courseCode("CRS-DUP-001")
            .title("Java Advanced")
            .durationWeeks(12)
            .fee(new BigDecimal("25000.00"))
            .active(true)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> courseRepository.saveAndFlush(duplicate));
    }

    @Test
    void shouldFindActiveCoursesAndSearchByTitleOrCode() {
        Course activeB = courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-B")
            .title("Backend Engineering")
            .durationWeeks(12)
            .fee(new BigDecimal("40000.00"))
            .active(true)
            .build());
        courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-A")
            .title("Angular UI")
            .durationWeeks(10)
            .fee(new BigDecimal("30000.00"))
            .active(true)
            .build());
        courseRepository.saveAndFlush(Course.builder()
            .courseCode("CRS-OLD")
            .title("Archived Course")
            .durationWeeks(4)
            .fee(new BigDecimal("10000.00"))
            .active(false)
            .build());

        List<Course> activeCourses = courseRepository.findByActiveTrueOrderByTitleAsc();
        assertThat(activeCourses).extracting(Course::getCourseCode).containsExactly("CRS-A", "CRS-B");

        assertThat(courseRepository.existsByCourseCode("CRS-B")).isTrue();
        assertThat(courseRepository.existsByCourseCodeAndIdNot("CRS-B", activeB.getId())).isFalse();
        assertThat(courseRepository.findByTitleContainingIgnoreCaseOrCourseCodeContainingIgnoreCase("angular", "angular"))
            .hasSize(1);
    }
}
