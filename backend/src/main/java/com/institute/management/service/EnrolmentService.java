package com.institute.management.service;

import com.institute.management.dto.request.AttendanceUpdateRequest;
import com.institute.management.dto.request.EnrolmentRequest;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Enrolment;
import com.institute.management.entity.Student;
import com.institute.management.enums.BatchStatus;
import com.institute.management.enums.EnrolmentStatus;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrolmentService {

    private static final BigDecimal MIN_ATTENDANCE = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal MAX_ATTENDANCE = new BigDecimal("100.00");

    private final EnrolmentRepository enrolmentRepository;
    private final StudentRepository studentRepository;
    private final BatchRepository batchRepository;
    private final EntityMapper entityMapper;

    public List<EnrolmentResponse> getAllEnrolments(Long batchId, Long studentId) {
        List<Enrolment> enrolments;
        if (batchId != null) {
            enrolments = enrolmentRepository.findByBatchIdWithDetails(batchId);
        } else if (studentId != null) {
            enrolments = enrolmentRepository.findByStudentIdWithDetails(studentId);
        } else {
            enrolments = enrolmentRepository.findAllWithDetails();
        }
        return enrolments.stream()
            .filter(e -> batchId == null || e.getBatch().getId().equals(batchId))
            .filter(e -> studentId == null || e.getStudent().getId().equals(studentId))
            .map(entityMapper::toEnrolmentResponse)
            .toList();
    }

    public EnrolmentResponse getEnrolmentById(Long id) {
        return entityMapper.toEnrolmentResponse(findEnrolmentOrThrow(id));
    }

    @Transactional
    public EnrolmentResponse createEnrolment(EnrolmentRequest request) {
        Student student = studentRepository.findById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));
        Batch batch = batchRepository.findByIdWithCourseAndTrainer(request.batchId())
            .orElseThrow(() -> new ResourceNotFoundException("Batch", request.batchId()));

        validateEligibleStudentAndBatch(student, batch);

        if (enrolmentRepository.existsByStudentIdAndBatchId(student.getId(), batch.getId())) {
            throw new DuplicateResourceException(
                "Student '" + student.getStudentCode() + "' is already enrolled in batch '" + batch.getBatchCode() + "'"
            );
        }

        EnrolmentStatus targetStatus = request.status() != null ? request.status() : EnrolmentStatus.ENROLLED;
        if (targetStatus == EnrolmentStatus.ENROLLED) {
            ensureBatchHasAvailableCapacity(batch);
        }

        BigDecimal attendance = normalizeAttendance(request.attendancePercentage());

        Enrolment enrolment = Enrolment.builder()
            .student(student)
            .batch(batch)
            .enrolmentDate(request.enrolmentDate() != null ? request.enrolmentDate() : LocalDate.now())
            .status(targetStatus)
            .attendancePercentage(attendance)
            .remarks(trimToNull(request.remarks()))
            .build();

        return entityMapper.toEnrolmentResponse(enrolmentRepository.save(enrolment));
    }

    @Transactional
    public EnrolmentResponse updateEnrolment(Long id, EnrolmentRequest request) {
        Enrolment existing = findEnrolmentOrThrow(id);
        Student student = studentRepository.findById(request.studentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", request.studentId()));
        Batch batch = batchRepository.findByIdWithCourseAndTrainer(request.batchId())
            .orElseThrow(() -> new ResourceNotFoundException("Batch", request.batchId()));

        if (enrolmentRepository.existsByStudentIdAndBatchIdAndIdNot(student.getId(), batch.getId(), id)) {
            throw new DuplicateResourceException(
                "Student '" + student.getStudentCode() + "' is already enrolled in batch '" + batch.getBatchCode() + "'"
            );
        }

        EnrolmentStatus targetStatus = request.status() != null ? request.status() : existing.getStatus();
        boolean batchChanged = !existing.getBatch().getId().equals(batch.getId());
        boolean studentChanged = !existing.getStudent().getId().equals(student.getId());
        boolean activatingSeat = targetStatus == EnrolmentStatus.ENROLLED
            && (existing.getStatus() != EnrolmentStatus.ENROLLED || batchChanged);

        if (studentChanged || activatingSeat) {
            validateEligibleStudentAndBatch(student, batch);
        }
        if (activatingSeat) {
            ensureBatchHasAvailableCapacity(batch);
        }

        existing.setStudent(student);
        existing.setBatch(batch);
        if (request.enrolmentDate() != null) {
            existing.setEnrolmentDate(request.enrolmentDate());
        }
        existing.setStatus(targetStatus);
        if (request.attendancePercentage() != null) {
            existing.setAttendancePercentage(normalizeAttendance(request.attendancePercentage()));
        }
        existing.setRemarks(trimToNull(request.remarks()));

        return entityMapper.toEnrolmentResponse(enrolmentRepository.save(existing));
    }

    @Transactional
    public EnrolmentResponse updateAttendance(Long id, AttendanceUpdateRequest request) {
        Enrolment existing = findEnrolmentOrThrow(id);
        BigDecimal attendance = normalizeAttendance(request.attendancePercentage());
        existing.setAttendancePercentage(attendance);
        if (request.remarks() != null) {
            existing.setRemarks(trimToNull(request.remarks()));
        }
        return entityMapper.toEnrolmentResponse(enrolmentRepository.save(existing));
    }

    @Transactional
    public void deleteEnrolment(Long id) {
        Enrolment existing = findEnrolmentOrThrow(id);
        enrolmentRepository.delete(existing);
    }

    private Enrolment findEnrolmentOrThrow(Long id) {
        return enrolmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Enrolment", id));
    }

    private void validateEligibleStudentAndBatch(Student student, Batch batch) {
        if (student.getStatus() == StudentStatus.INACTIVE || student.getStatus() == StudentStatus.DROPPED) {
            throw new BusinessRuleException(
                "Cannot enrol student '" + student.getStudentCode() + "' with status " + student.getStatus(),
                HttpStatus.CONFLICT
            );
        }
        if (batch.getStatus() == BatchStatus.CANCELLED || batch.getStatus() == BatchStatus.COMPLETED) {
            throw new BusinessRuleException(
                "Cannot enrol students in a " + batch.getStatus() + " batch (" + batch.getBatchCode() + ")",
                HttpStatus.CONFLICT
            );
        }
    }

    private void ensureBatchHasAvailableCapacity(Batch batch) {
        long enrolledCount = enrolmentRepository.countByBatchIdAndStatus(batch.getId(), EnrolmentStatus.ENROLLED);
        if (enrolledCount >= batch.getMaxCapacity()) {
            throw new BusinessRuleException(
                "Batch '" + batch.getBatchCode() + "' has reached its maximum capacity of "
                    + batch.getMaxCapacity() + " seats",
                HttpStatus.CONFLICT
            );
        }
    }

    private BigDecimal normalizeAttendance(BigDecimal value) {
        if (value == null) {
            return MIN_ATTENDANCE;
        }
        if (value.compareTo(MIN_ATTENDANCE) < 0 || value.compareTo(MAX_ATTENDANCE) > 0) {
            throw new BusinessRuleException(
                "Attendance percentage must be between 0.00 and 100.00",
                HttpStatus.BAD_REQUEST
            );
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
