package com.institute.management.service;

import com.institute.management.dto.request.BatchRequest;
import com.institute.management.dto.response.BatchResponse;
import com.institute.management.dto.response.BatchRosterResponse;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Trainer;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.CourseRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final TrainerRepository trainerRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final EntityMapper entityMapper;

    public List<BatchResponse> getAllBatches(BatchStatus status, Long courseId, Long trainerId) {
        List<Batch> batches = batchRepository.findAllWithCourseAndTrainer();
        Map<Long, Long> enrolledCounts = getEnrolledCountMap();

        return batches.stream()
            .filter(batch -> status == null || batch.getStatus() == status)
            .filter(batch -> courseId == null || batch.getCourse().getId().equals(courseId))
            .filter(batch -> trainerId == null || batch.getTrainer().getId().equals(trainerId))
            .map(batch -> entityMapper.toBatchResponse(batch, enrolledCounts.getOrDefault(batch.getId(), 0L)))
            .toList();
    }

    public BatchResponse getBatchById(Long id) {
        Batch batch = findBatchWithDetailsOrThrow(id);
        long enrolledCount = enrolmentRepository.countByBatchIdAndStatus(id, EnrolmentStatus.ENROLLED);
        return entityMapper.toBatchResponse(batch, enrolledCount);
    }

    public BatchRosterResponse getBatchRoster(Long id) {
        BatchResponse batchResponse = getBatchById(id);
        List<EnrolmentResponse> enrolments = enrolmentRepository.findByBatchIdWithDetails(id).stream()
            .map(entityMapper::toEnrolmentResponse)
            .toList();
        BigDecimal avgAttendance = enrolmentRepository.findAverageAttendanceByBatchId(id)
            .setScale(2, RoundingMode.HALF_UP);
        return new BatchRosterResponse(batchResponse, avgAttendance, enrolments);
    }

    @Transactional
    public BatchResponse createBatch(BatchRequest request) {
        String batchCode = request.batchCode().trim();
        if (batchRepository.existsByBatchCode(batchCode)) {
            throw new DuplicateResourceException("Batch code already exists: " + batchCode);
        }

        validateDateRange(request.startDate(), request.endDate());

        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", request.courseId()));
        Trainer trainer = trainerRepository.findById(request.trainerId())
            .orElseThrow(() -> new ResourceNotFoundException("Trainer", request.trainerId()));

        validateActiveCourseAndTrainer(course, trainer);

        Batch batch = Batch.builder()
            .batchCode(batchCode)
            .batchName(request.batchName().trim())
            .course(course)
            .trainer(trainer)
            .startDate(request.startDate())
            .endDate(request.endDate())
            .scheduleTimings(request.scheduleTimings().trim())
            .mode(request.mode())
            .maxCapacity(request.maxCapacity())
            .status(request.status() != null ? request.status() : BatchStatus.UPCOMING)
            .build();

        Batch saved = batchRepository.save(batch);
        return entityMapper.toBatchResponse(saved, 0L);
    }

    @Transactional
    public BatchResponse updateBatch(Long id, BatchRequest request) {
        Batch existing = findBatchWithDetailsOrThrow(id);
        String batchCode = request.batchCode().trim();

        if (batchRepository.existsByBatchCodeAndIdNot(batchCode, id)) {
            throw new DuplicateResourceException("Batch code already exists: " + batchCode);
        }

        validateDateRange(request.startDate(), request.endDate());

        Course course = courseRepository.findById(request.courseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", request.courseId()));
        Trainer trainer = trainerRepository.findById(request.trainerId())
            .orElseThrow(() -> new ResourceNotFoundException("Trainer", request.trainerId()));

        BatchStatus targetStatus = request.status() != null ? request.status() : existing.getStatus();
        boolean isActiveSchedule = targetStatus == BatchStatus.UPCOMING || targetStatus == BatchStatus.ONGOING;
        boolean courseChanged = !existing.getCourse().getId().equals(course.getId());
        boolean trainerChanged = !existing.getTrainer().getId().equals(trainer.getId());

        if (isActiveSchedule || courseChanged) {
            if (!Boolean.TRUE.equals(course.getActive())) {
                throw new BusinessRuleException(
                    "Cannot schedule a batch using an inactive course: " + course.getCourseCode(),
                    HttpStatus.CONFLICT
                );
            }
        }
        if (isActiveSchedule || trainerChanged) {
            if (!Boolean.TRUE.equals(trainer.getActive())) {
                throw new BusinessRuleException(
                    "Cannot schedule a batch using an inactive trainer: " + trainer.getEmployeeCode(),
                    HttpStatus.CONFLICT
                );
            }
        }

        long enrolledCount = enrolmentRepository.countByBatchIdAndStatus(id, EnrolmentStatus.ENROLLED);
        if (request.maxCapacity() < enrolledCount) {
            throw new BusinessRuleException(
                "Max capacity (" + request.maxCapacity() + ") cannot be less than current enrolled students ("
                    + enrolledCount + ")",
                HttpStatus.CONFLICT
            );
        }

        existing.setBatchCode(batchCode);
        existing.setBatchName(request.batchName().trim());
        existing.setCourse(course);
        existing.setTrainer(trainer);
        existing.setStartDate(request.startDate());
        existing.setEndDate(request.endDate());
        existing.setScheduleTimings(request.scheduleTimings().trim());
        existing.setMode(request.mode());
        existing.setMaxCapacity(request.maxCapacity());
        existing.setStatus(targetStatus);

        Batch saved = batchRepository.save(existing);
        return entityMapper.toBatchResponse(saved, enrolledCount);
    }

    @Transactional
    public void deleteBatch(Long id) {
        Batch existing = batchRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch", id));
        if (enrolmentRepository.existsByBatchId(id)) {
            throw new BusinessRuleException(
                "Cannot delete batch '" + existing.getBatchCode() + "' because it has existing enrolments",
                HttpStatus.CONFLICT
            );
        }
        batchRepository.delete(existing);
    }

    public Map<Long, Long> getEnrolledCountMap() {
        Map<Long, Long> countMap = new HashMap<>();
        for (Object[] row : enrolmentRepository.countGroupedByBatchIdAndStatus(EnrolmentStatus.ENROLLED)) {
            Long batchId = (Long) row[0];
            Long count = (Long) row[1];
            countMap.put(batchId, count);
        }
        return countMap;
    }

    private Batch findBatchWithDetailsOrThrow(Long id) {
        return batchRepository.findByIdWithCourseAndTrainer(id)
            .orElseThrow(() -> new ResourceNotFoundException("Batch", id));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleException(
                "Batch end date cannot be before start date",
                HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateActiveCourseAndTrainer(Course course, Trainer trainer) {
        if (!Boolean.TRUE.equals(course.getActive())) {
            throw new BusinessRuleException(
                "Cannot schedule a batch using an inactive course: " + course.getCourseCode(),
                HttpStatus.CONFLICT
            );
        }
        if (!Boolean.TRUE.equals(trainer.getActive())) {
            throw new BusinessRuleException(
                "Cannot schedule a batch using an inactive trainer: " + trainer.getEmployeeCode(),
                HttpStatus.CONFLICT
            );
        }
    }
}
