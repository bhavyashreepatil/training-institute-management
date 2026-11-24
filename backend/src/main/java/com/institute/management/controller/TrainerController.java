package com.institute.management.controller;

import com.institute.management.dto.request.TrainerRequest;
import com.institute.management.dto.response.TrainerResponse;
import com.institute.management.service.TrainerService;
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
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @GetMapping
    public ResponseEntity<List<TrainerResponse>> getAllTrainers(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Boolean activeOnly
    ) {
        return ResponseEntity.ok(trainerService.getAllTrainers(search, activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponse> getTrainerById(@PathVariable Long id) {
        return ResponseEntity.ok(trainerService.getTrainerById(id));
    }

    @PostMapping
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainerService.createTrainer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerResponse> updateTrainer(
        @PathVariable Long id,
        @Valid @RequestBody TrainerRequest request
    ) {
        return ResponseEntity.ok(trainerService.updateTrainer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}
