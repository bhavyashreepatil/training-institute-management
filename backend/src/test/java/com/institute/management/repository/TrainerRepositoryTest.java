package com.institute.management.repository;

import com.institute.management.entity.Trainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("test")
class TrainerRepositoryTest {

    @Autowired
    private TrainerRepository trainerRepository;

    @Test
    void shouldRejectDuplicateEmployeeCodeAndDuplicateEmail() {
        trainerRepository.saveAndFlush(Trainer.builder()
            .employeeCode("TRN-100")
            .fullName("Aarav Mehta")
            .email("aarav@institute.edu")
            .phone("+919876543210")
            .specialization("Java")
            .experienceYears(10)
            .active(true)
            .build());

        Trainer duplicateCode = Trainer.builder()
            .employeeCode("TRN-100")
            .fullName("Other Trainer")
            .email("other@institute.edu")
            .phone("+919876543211")
            .specialization("Angular")
            .experienceYears(5)
            .active(true)
            .build();
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.saveAndFlush(duplicateCode));

        Trainer duplicateEmail = Trainer.builder()
            .employeeCode("TRN-101")
            .fullName("Duplicate Email Trainer")
            .email("aarav@institute.edu")
            .phone("+919876543212")
            .specialization("Python")
            .experienceYears(6)
            .active(true)
            .build();
        assertThrows(DataIntegrityViolationException.class, () -> trainerRepository.saveAndFlush(duplicateEmail));
    }

    @Test
    void shouldCountAndFilterActiveTrainers() {
        Trainer active = trainerRepository.saveAndFlush(Trainer.builder()
            .employeeCode("TRN-201")
            .fullName("Priya Nair")
            .email("priya@institute.edu")
            .phone("+919876543221")
            .specialization("Angular & TypeScript")
            .experienceYears(8)
            .active(true)
            .build());
        trainerRepository.saveAndFlush(Trainer.builder()
            .employeeCode("TRN-202")
            .fullName("Vikram Rao")
            .email("vikram@institute.edu")
            .phone("+919876543222")
            .specialization("Legacy")
            .experienceYears(14)
            .active(false)
            .build());

        List<Trainer> activeTrainers = trainerRepository.findByActiveTrueOrderByFullNameAsc();
        assertThat(activeTrainers).hasSize(1);
        assertThat(activeTrainers.get(0).getEmployeeCode()).isEqualTo("TRN-201");
        assertThat(trainerRepository.countByActiveTrue()).isEqualTo(1L);
        assertThat(trainerRepository.existsByEmail("priya@institute.edu")).isTrue();
        assertThat(trainerRepository.existsByEmailAndIdNot("priya@institute.edu", active.getId())).isFalse();
    }
}
