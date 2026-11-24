package com.institute.management.service;

import com.institute.management.dto.request.TrainerRequest;
import com.institute.management.dto.response.TrainerResponse;
import com.institute.management.entity.Trainer;
import com.institute.management.exception.BusinessRuleException;
import com.institute.management.exception.DuplicateResourceException;
import com.institute.management.mapper.EntityMapper;
import com.institute.management.repository.BatchRepository;
import com.institute.management.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private BatchRepository batchRepository;

    private TrainerService trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerService(trainerRepository, batchRepository, new EntityMapper());
    }

    @Test
    void createTrainerShouldNormalizeEmailAndPersist() {
        TrainerRequest request = new TrainerRequest(
            "TRN-01", "Aarav Mehta", "Aarav@Institute.EDU", "+919876543210", "Java", 9, true
        );
        when(trainerRepository.existsByEmployeeCode("TRN-01")).thenReturn(false);
        when(trainerRepository.existsByEmail("aarav@institute.edu")).thenReturn(false);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        TrainerResponse response = trainerService.createTrainer(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("aarav@institute.edu");
    }

    @Test
    void createTrainerShouldRejectDuplicateEmail() {
        TrainerRequest request = new TrainerRequest(
            "TRN-02", "Priya Nair", "priya@institute.edu", "+919876543211", "Angular", 7, true
        );
        when(trainerRepository.existsByEmployeeCode("TRN-02")).thenReturn(false);
        when(trainerRepository.existsByEmail("priya@institute.edu")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> trainerService.createTrainer(request));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void deleteTrainerShouldThrowConflictWhenAssignedToBatches() {
        Trainer existing = Trainer.builder().id(5L).employeeCode("TRN-05").fullName("Trainer").build();
        when(trainerRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(batchRepository.existsByTrainerId(5L)).thenReturn(true);

        BusinessRuleException ex = assertThrows(BusinessRuleException.class, () -> trainerService.deleteTrainer(5L));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
    }
}
