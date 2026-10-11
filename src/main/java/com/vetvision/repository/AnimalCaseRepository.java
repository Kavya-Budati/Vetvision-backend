package com.vetvision.repository;

import com.vetvision.entity.AnimalCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimalCaseRepository extends JpaRepository<AnimalCase, Long> {
    List<AnimalCase> findAllByOrderByReportedAtDesc();
    List<AnimalCase> findByReporter_EmailIgnoreCaseOrderByReportedAtDesc(String email);
    List<AnimalCase> findByAssignedClinic_Owner_EmailIgnoreCaseOrderByReportedAtDesc(String email);
    Optional<AnimalCase> findByCaseId(String caseId);
}