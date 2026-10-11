package com.vetvision.dto;

import com.vetvision.entity.AnimalCase;
import com.vetvision.entity.CaseStatus;

import java.time.LocalDateTime;

public record CaseResponse(Long id, String caseId, String animalType, String breed, String description,
                           String symptoms, String location, String urgency, CaseStatus status,
                           LocalDateTime reportedAt, String reporterName, Long clinicId, String clinicName) {
    public static CaseResponse from(AnimalCase animalCase) {
        return new CaseResponse(animalCase.getId(), animalCase.getCaseId(), animalCase.getAnimalType(),
                animalCase.getBreed(), animalCase.getDescription(), animalCase.getSymptoms(), animalCase.getLocation(),
                animalCase.getUrgency(), animalCase.getStatus(), animalCase.getReportedAt(),
                animalCase.getReporter().getFirstName() + " " + animalCase.getReporter().getLastName(),
                animalCase.getAssignedClinic() == null ? null : animalCase.getAssignedClinic().getId(),
                animalCase.getAssignedClinic() == null ? null : animalCase.getAssignedClinic().getName());
    }
}