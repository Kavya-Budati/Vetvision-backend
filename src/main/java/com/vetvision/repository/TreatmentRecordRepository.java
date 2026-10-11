package com.vetvision.repository;

import com.vetvision.entity.TreatmentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreatmentRecordRepository extends JpaRepository<TreatmentRecord, Long> {
    List<TreatmentRecord> findByAnimalCase_IdOrderByTreatmentDateDesc(Long caseId);
}