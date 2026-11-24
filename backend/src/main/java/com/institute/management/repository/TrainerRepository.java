package com.institute.management.repository;

import com.institute.management.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Trainer> findByEmployeeCode(String employeeCode);

    List<Trainer> findByActiveTrueOrderByFullNameAsc();

    long countByActiveTrue();

    List<Trainer> findByFullNameContainingIgnoreCaseOrEmployeeCodeContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
        String fullName,
        String employeeCode,
        String specialization
    );
}
