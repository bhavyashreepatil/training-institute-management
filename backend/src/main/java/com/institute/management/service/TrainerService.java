package com.institute.management.service;

import com.institute.management.dto.request.TrainerRequest;
import com.institute.management.dto.response.TrainerResponse;
import com.institute.management.entity.Trainer;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.exception.ResourceNotFoundException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final BatchRepository batchRepository;
    private final EntityMapper entityMapper;

    public List<TrainerResponse> getAllTrainers(String search, Boolean activeOnly) {
        List<Trainer> trainers;
        if (search != null && !search.isBlank()) {
            String term = search.trim();
            trainers = trainerRepository
                .findByFullNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
                    term, term, term
                );
        } else if (Boolean.TRUE.equals(activeOnly)) {
            trainers = trainerRepository.findByActiveTrueOrderByFullNameAsc();
        } else {
            trainers = trainerRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        return trainers.stream()
            .filter(trainer -> !Boolean.TRUE.equals(activeOnly) || Boolean.TRUE.equals(trainer.getActive()))
            .map(entityMapper::toTrainerResponse)
            .toList();
    }

    public TrainerResponse getTrainerById(Long id) {
        return entityMapper.toTrainerResponse(findTrainerOrThrow(id));
    }

    @Transactional
    public TrainerResponse createTrainer(TrainerRequest request) {
        String employeeCode = request.employeeCode().trim();
        String email = request.email().trim().toLowerCase();

        if (trainerRepository.existsByEmployeeCode(employeeCode)) {
            throw new DuplicateResourceException("Employee code already exists: " + employeeCode);
        }
        if (trainerRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Trainer email already exists: " + email);
        }

        Trainer trainer = Trainer.builder()
            .employeeCode(employeeCode)
            .fullName(request.fullName().trim())
            .email(email)
            .phone(request.phone().trim())
            .specialization(request.specialization().trim())
            .experienceYears(request.experienceYears())
            .active(request.active() != null ? request.active() : Boolean.TRUE)
            .build();

        return entityMapper.toTrainerResponse(trainerRepository.save(trainer));
    }

    @Transactional
    public TrainerResponse updateTrainer(Long id, TrainerRequest request) {
        Trainer existing = findTrainerOrThrow(id);
        String employeeCode = request.employeeCode().trim();
        String email = request.email().trim().toLowerCase();

        if (trainerRepository.existsByEmployeeCodeAndIdNot(employeeCode, id)) {
            throw new DuplicateResourceException("Employee code already exists: " + employeeCode);
        }
        if (trainerRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Trainer email already exists: " + email);
        }

        existing.setEmployeeCode(employeeCode);
        existing.setFullName(request.fullName().trim());
        existing.setEmail(email);
        existing.setPhone(request.phone().trim());
        existing.setSpecialization(request.specialization().trim());
        existing.setExperienceYears(request.experienceYears());
        if (request.active() != null) {
            existing.setActive(request.active());
        }

        return entityMapper.toTrainerResponse(trainerRepository.save(existing));
    }

    @Transactional
    public void deleteTrainer(Long id) {
        Trainer existing = findTrainerOrThrow(id);
        if (batchRepository.existsByTrainerId(id)) {
            throw new BusinessRuleException(
                "Cannot delete trainer '" + existing.getEmployeeCode() + "' because they are assigned to existing batches",
                HttpStatus.CONFLICT
            );
        }
        trainerRepository.delete(existing);
    }

    private Trainer findTrainerOrThrow(Long id) {
        return trainerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Trainer", id));
    }
}
