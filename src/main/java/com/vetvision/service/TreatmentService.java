package com.vetvision.service;

import com.vetvision.dto.TreatmentRequest;
import com.vetvision.dto.TreatmentResponse;
import com.vetvision.entity.CaseStatus;
import com.vetvision.entity.Role;
import com.vetvision.entity.TreatmentRecord;
import com.vetvision.repository.TreatmentRecordRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TreatmentService {
    private final TreatmentRecordRepository treatments;
    private final CaseService cases;

    public TreatmentService(TreatmentRecordRepository treatments, CaseService cases) {
        this.treatments = treatments;
        this.cases = cases;
    }

    @Transactional(readOnly = true)
    public List<TreatmentResponse> list(Long caseId, String email) {
        cases.accessibleCase(caseId, email);
        return treatments.findByAnimalCase_IdOrderByTreatmentDateDesc(caseId).stream().map(TreatmentResponse::from).toList();
    }

    @Transactional
    public TreatmentResponse create(TreatmentRequest request, String email) {
        var user = cases.user(email);
        if (user.getRole() != Role.CLINIC) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only clinic users can add treatment records.");
        var animalCase = cases.accessibleCase(request.caseId(), email);
        if (animalCase.getAssignedClinic() == null || animalCase.getAssignedClinic().getOwner() == null
                || !animalCase.getAssignedClinic().getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This case is not assigned to your clinic.");
        }
        TreatmentRecord record = new TreatmentRecord();
        record.setAnimalCase(animalCase);
        record.setClinic(animalCase.getAssignedClinic());
        record.setVeterinarian(user);
        record.setDiagnosis(request.diagnosis().trim());
        record.setTreatment(request.treatment().trim());
        record.setCareInstructions(request.careInstructions());
        record.setTreatmentDate(request.treatmentDate());
        record.setFollowUpDate(request.followUpDate());
        if (animalCase.getStatus() == CaseStatus.AT_CLINIC) animalCase.setStatus(CaseStatus.UNDER_TREATMENT);
        return TreatmentResponse.from(treatments.save(record));
    }
}