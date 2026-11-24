package com.institute.management.service;

import com.institute.management.dto.request.StudentRequest;
import com.institute.management.dto.response.StudentResponse;
import com.institute.management.entity.Student;
import com.institute.management.enums.StudentStatus;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.EnrolmentRepository;
import com.institute.management.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrolmentRepository enrolmentRepository;
    private final EntityMapper entityMapper;

    public List<StudentResponse> getAllStudents(String search, StudentStatus status) {
        List<Student> students;
        if (search != null && !search.isBlank()) {
            String term = search.trim();
            students = studentRepository
                .findByFullNameContainingIgnoreCaseOrStudentCodeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    term, term, term
                );
        } else if (status != null) {
            students = studentRepository.findByStatusOrderByFullNameAsc(status);
        } else {
            students = studentRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return students.stream()
            .filter(student -> status == null || student.getStatus() == status)
            .map(entityMapper::toStudentResponse)
            .toList();
    }

    public StudentResponse getStudentById(Long id) {
        return entityMapper.toStudentResponse(findStudentOrThrow(id));
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        String studentCode = request.studentCode().trim();
        String email = request.email().trim().toLowerCase();

        if (studentRepository.existsByStudentCode(studentCode)) {
            throw new DuplicateResourceException("Student code already exists: " + studentCode);
        }
        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student email already exists: " + email);
        }

        Student student = Student.builder()
            .studentCode(studentCode)
            .fullName(request.fullName().trim())
            .email(email)
            .phone(request.phone().trim())
            .dateOfBirth(request.dateOfBirth())
            .status(request.status() != null ? request.status() : StudentStatus.ACTIVE)
            .build();

        return entityMapper.toStudentResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student existing = findStudentOrThrow(id);
        String studentCode = request.studentCode().trim();
        String email = request.email().trim().toLowerCase();

        if (studentRepository.existsByStudentCodeAndIdNot(studentCode, id)) {
            throw new DuplicateResourceException("Student code already exists: " + studentCode);
        }
        if (studentRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Student email already exists: " + email);
        }

        existing.setStudentCode(studentCode);
        existing.setFullName(request.fullName().trim());
        existing.setEmail(email);
        existing.setPhone(request.phone().trim());
        existing.setDateOfBirth(request.dateOfBirth());
        if (request.status() != null) {
            existing.setStatus(request.status());
        }

        return entityMapper.toStudentResponse(studentRepository.save(existing));
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student existing = findStudentOrThrow(id);
        if (enrolmentRepository.existsByStudentId(id)) {
            throw new BusinessRuleException(
                "Cannot delete student '" + existing.getStudentCode() + "' because they have existing enrolments",
                HttpStatus.CONFLICT
            );
        }
        studentRepository.delete(existing);
    }

    private Student findStudentOrThrow(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }
}
