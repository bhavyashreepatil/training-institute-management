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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;
    private final EntityMapper entityMapper;

    public List<CourseResponse> getAllCourses(String search, Boolean activeOnly) {
        List<Course> courses;
        if (search != null && !search.isBlank()) {
            String term = search.trim();
            courses = courseRepository.findByTitleContainingIgnoreCaseOrCourseCodeContainingIgnoreCase(term, term);
        } else if (Boolean.TRUE.equals(activeOnly)) {
            courses = courseRepository.findByActiveTrueOrderByTitleAsc();
        } else {
            courses = courseRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return courses.stream()
            .filter(course -> !Boolean.TRUE.equals(activeOnly) || Boolean.TRUE.equals(course.getActive()))
            .map(entityMapper::toCourseResponse)
            .toList();
    }

    public CourseResponse getCourseById(Long id) {
        return entityMapper.toCourseResponse(findCourseOrThrow(id));
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        String courseCode = request.courseCode().trim();
        if (courseRepository.existsByCourseCode(courseCode)) {
            throw new DuplicateResourceException("Course code already exists: " + courseCode);
        }

        Course course = Course.builder()
            .courseCode(courseCode)
            .title(request.title().trim())
            .description(trimToNull(request.description()))
            .durationWeeks(request.durationWeeks())
            .fee(request.fee())
            .active(request.active() != null ? request.active() : Boolean.TRUE)
            .build();

        return entityMapper.toCourseResponse(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course existing = findCourseOrThrow(id);
        String courseCode = request.courseCode().trim();

        if (courseRepository.existsByCourseCodeAndIdNot(courseCode, id)) {
            throw new DuplicateResourceException("Course code already exists: " + courseCode);
        }

        existing.setCourseCode(courseCode);
        existing.setTitle(request.title().trim());
        existing.setDescription(trimToNull(request.description()));
        existing.setDurationWeeks(request.durationWeeks());
        existing.setFee(request.fee());
        if (request.active() != null) {
            existing.setActive(request.active());
        }

        return entityMapper.toCourseResponse(courseRepository.save(existing));
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course existing = findCourseOrThrow(id);
        if (batchRepository.existsByCourseId(id)) {
            throw new BusinessRuleException(
                "Cannot delete course '" + existing.getCourseCode() + "' because it is referenced by existing batches",
                HttpStatus.CONFLICT
            );
        }
        courseRepository.delete(existing);
    }

    private Course findCourseOrThrow(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
