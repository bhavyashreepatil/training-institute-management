package com.institute.management.repository;

import com.institute.management.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseCode(String courseCode);

    boolean existsByCourseCodeAndIdNot(String courseCode, Long id);

    Optional<Course> findByCourseCode(String courseCode);

    List<Course> findByActiveTrueOrderByTitleAsc();

    long countByActiveTrue();

    List<Course> findByTitleContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(String title, String courseCode);
}
