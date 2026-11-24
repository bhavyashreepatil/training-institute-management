package com.institute.management.controller;

import com.institute.management.dto.request.BatchRequest;
import com.institute.management.dto.response.BatchResponse;
import com.institute.management.dto.response.BatchRosterResponse;
import com.institute.management.enums.BatchStatus;
import com.institute.management.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAllBatches(
        @RequestParam(required = false) BatchStatus status,
        @RequestParam(required = false) Long courseId,
        @RequestParam(required = false) Long trainerId
    ) {
        return ResponseEntity.ok(batchService.getAllBatches(status, courseId, trainerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @GetMapping("/{id}/roster")
    public ResponseEntity<BatchRosterResponse> getBatchRoster(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.getBatchRoster(id));
    }

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody BatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(batchService.createBatch(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> updateBatch(
        @PathVariable Long id,
        @Valid @RequestBody BatchRequest request
    ) {
        return ResponseEntity.ok(batchService.updateBatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
