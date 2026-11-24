package com.institute.management.mapper;

import com.institute.management.dto.response.BatchResponse;
import com.institute.management.dto.response.CourseResponse;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.dto.response.StudentResponse;
import com.institute.management.dto.response.TrainerResponse;
import com.institute.management.entity.Batch;
import com.institute.management.entity.Course;
import com.institute.management.entity.Enrolment;
import com.institute.management.entity.Student;
import com.institute.management.entity.Trainer;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(
            course.getId(),
            course.getCourseCode(),
            course.getTitle(),
            course.getDescription(),
            course.getDurationWeeks(),
            course.getFee(),
            course.getActive(),
            course.getCreatedAt(),
            course.getUpdatedAt()
        );
    }

    public TrainerResponse toTrainerResponse(Trainer trainer) {
        return new TrainerResponse(
            trainer.getId(),
            trainer.getEmployeeCode(),
            trainer.getFullName(),
            trainer.getEmail(),
            trainer.getPhone(),
            trainer.getSpecialization(),
            trainer.getExperienceYears(),
            trainer.getActive(),
            trainer.getCreatedAt(),
            trainer.getUpdatedAt()
        );
    }

    public StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getStudentCode(),
            student.getFullName(),
            student.getEmail(),
            student.getPhone(),
            student.getDateOfBirth(),
            student.getStatus(),
            student.getCreatedAt(),
            student.getUpdatedAt()
        );
    }

    public BatchResponse toBatchResponse(Batch batch, long enrolledCount) {
        long availableSeats = Math.max(0L, batch.getMaxCapacity() - enrolledCount);
        return new BatchResponse(
            batch.getId(),
            batch.getBatchCode(),
            batch.getBatchName(),
            batch.getCourse().getId(),
            batch.getCourse().getCourseCode(),
            batch.getCourse().getTitle(),
            batch.getTrainer().getId(),
            batch.getTrainer().getFullName(),
            batch.getTrainer().getEmployeeCode(),
            batch.getStartDate(),
            batch.getEndDate(),
            batch.getScheduleTimings(),
            batch.getMode(),
            batch.getMaxCapacity(),
            enrolledCount,
            availableSeats,
            batch.getStatus(),
            batch.getCreatedAt(),
            batch.getUpdatedAt()
        );
    }

    public EnrolmentResponse toEnrolmentResponse(Enrolment enrolment) {
        return new EnrolmentResponse(
            enrolment.getId(),
            enrolment.getStudent().getId(),
            enrolment.getStudent().getStudentCode(),
            enrolment.getStudent().getFullName(),
            enrolment.getStudent().getEmail(),
            enrolment.getStudent().getStatus(),
            enrolment.getBatch().getId(),
            enrolment.getBatch().getBatchCode(),
            enrolment.getBatch().getBatchName(),
            enrolment.getBatch().getCourse().getTitle(),
            enrolment.getEnrolmentDate(),
            enrolment.getStatus(),
            enrolment.getAttendancePercentage(),
            enrolment.getRemarks(),
            enrolment.getCreatedAt(),
            enrolment.getUpdatedAt()
        );
    }
}
