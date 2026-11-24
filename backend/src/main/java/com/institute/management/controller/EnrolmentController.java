package com.institute.management.controller;

import com.institute.management.dto.request.AttendanceUpdateRequest;
import com.institute.management.dto.request.EnrolmentRequest;
import com.institute.management.dto.response.EnrolmentResponse;
import com.institute.management.service.EnrolmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrolments")
@RequiredArgsConstructor
public class EnrolmentController {

    private final EnrolmentService enrolmentService;

    @GetMapping
    public ResponseEntity<List<EnrolmentResponse>> getAllEnrolments(
        @RequestParam(required = false) Long batchId,
        @RequestParam(required = false) Long studentId
    ) {
        return ResponseEntity.ok(enrolmentService.getAllEnrolments(batchId, studentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrolmentResponse> getEnrolmentById(@PathVariable Long id) {
        return ResponseEntity.ok(enrolmentService.getEnrolmentById(id));
    }

    @PostMapping
    public ResponseEntity<EnrolmentResponse> createEnrolment(@Valid @RequestBody EnrolmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrolmentService.createEnrolment(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrolmentResponse> updateEnrolment(
        @PathVariable Long id,
        @Valid @RequestBody EnrolmentRequest request
    ) {
        return ResponseEntity.ok(enrolmentService.updateEnrolment(id, request));
    }

    @PatchMapping("/{id}/attendance")
    public ResponseEntity<EnrolmentResponse> updateAttendance(
        @PathVariable Long id,
        @Valid @RequestBody AttendanceUpdateRequest request
    ) {
        return ResponseEntity.ok(enrolmentService.updateAttendance(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrolment(@PathVariable Long id) {
        enrolmentService.deleteEnrolment(id);
        return ResponseEntity.noContent().build();
    }
}
