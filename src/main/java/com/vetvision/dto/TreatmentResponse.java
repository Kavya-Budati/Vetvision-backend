package com.vetvision.dto;

import com.vetvision.entity.TreatmentRecord;

import java.time.LocalDate;

public record TreatmentResponse(Long id, Long caseId, String diagnosis, String treatment,
                                String careInstructions, LocalDate treatmentDate, LocalDate followUpDate,
                                String clinicName, String veterinarianName) {
    public static TreatmentResponse from(TreatmentRecord record) {
        return new TreatmentResponse(record.getId(), record.getAnimalCase().getId(), record.getDiagnosis(),
                record.getTreatment(), record.getCareInstructions(), record.getTreatmentDate(), record.getFollowUpDate(),
                record.getClinic().getName(), record.getVeterinarian().getFirstName() + " " + record.getVeterinarian().getLastName());
    }
}