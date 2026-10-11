package com.vetvision.service;

import com.vetvision.dto.CaseRequest;
import com.vetvision.dto.CaseResponse;
import com.vetvision.entity.AnimalCase;
import com.vetvision.entity.CaseStatus;
import com.vetvision.entity.Role;
import com.vetvision.entity.User;
import com.vetvision.entity.VeterinaryClinic;
import com.vetvision.repository.AnimalCaseRepository;
import com.vetvision.repository.UserRepository;
import com.vetvision.repository.VeterinaryClinicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@Service
public class CaseService {
    private final AnimalCaseRepository cases;
    private final UserRepository users;
    private final VeterinaryClinicRepository clinics;

    public CaseService(AnimalCaseRepository cases, UserRepository users, VeterinaryClinicRepository clinics) {
        this.cases = cases;
        this.users = users;
        this.clinics = clinics;
    }

    @Transactional
    public CaseResponse create(CaseRequest request, String email) {
        User reporter = user(email);
        AnimalCase animalCase = new AnimalCase();
        animalCase.setCaseId("VV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        animalCase.setAnimalType(request.animalType().trim());
        animalCase.setBreed(request.breed());
        animalCase.setDescription(request.description().trim());
        animalCase.setSymptoms(request.symptoms());
        animalCase.setLocation(request.location().trim());
        animalCase.setUrgency(request.urgency().trim().toUpperCase());
        animalCase.setReportedAt(LocalDateTime.now());
        animalCase.setStatus(CaseStatus.REPORTED);
        animalCase.setReporter(reporter);
        return CaseResponse.from(cases.save(animalCase));
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> list(String email) {
        User current = user(email);
        List<AnimalCase> result = switch (current.getRole()) {
            case ORGANIZATION -> cases.findAllByOrderByReportedAtDesc();
            case CLINIC -> cases.findByAssignedClinic_Owner_EmailIgnoreCaseOrderByReportedAtDesc(email);
            case RESCUER -> cases.findByReporter_EmailIgnoreCaseOrderByReportedAtDesc(email);
        };
        return result.stream().map(CaseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CaseResponse get(Long id, String email) {
        return CaseResponse.from(accessibleCase(id, email));
    }

    @Transactional
    public CaseResponse assign(Long id, Long clinicId, String email) {
        requireRole(email, Role.ORGANIZATION);
        AnimalCase animalCase = find(id);
        VeterinaryClinic clinic = clinics.findById(clinicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clinic not found."));
        animalCase.setAssignedClinic(clinic);
        if (animalCase.getStatus() == CaseStatus.REPORTED || animalCase.getStatus() == CaseStatus.UNDER_REVIEW) {
            animalCase.setStatus(CaseStatus.ASSIGNED);
        }
        return CaseResponse.from(cases.save(animalCase));
    }

    @Transactional
    public CaseResponse updateStatus(Long id, CaseStatus next, String email) {
        User current = user(email);
        AnimalCase animalCase = find(id);
        boolean org = current.getRole() == Role.ORGANIZATION;
        boolean assignedClinic = current.getRole() == Role.CLINIC && isOwnedBy(animalCase, email);
        boolean reporterCancel = current.getRole() == Role.RESCUER
                && animalCase.getReporter().getEmail().equalsIgnoreCase(email)
                && next == CaseStatus.CANCELLED
                && EnumSet.of(CaseStatus.REPORTED, CaseStatus.UNDER_REVIEW).contains(animalCase.getStatus());
        if (!org && !assignedClinic && !reporterCancel) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot update this case.");
        if (!allowed(animalCase.getStatus(), next)) throw new ResponseStatusException(HttpStatus.CONFLICT, "That case status transition is not allowed.");
        animalCase.setStatus(next);
        return CaseResponse.from(cases.save(animalCase));
    }

    @Transactional(readOnly = true)
    public AnimalCase accessibleCase(Long id, String email) {
        AnimalCase animalCase = find(id);
        User current = user(email);
        boolean allowed = current.getRole() == Role.ORGANIZATION
                || animalCase.getReporter().getEmail().equalsIgnoreCase(email)
                || (current.getRole() == Role.CLINIC && isOwnedBy(animalCase, email));
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot access this case.");
        return animalCase;
    }

    public User user(String email) {
        return users.findByEmailIgnoreCase(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account no longer exists."));
    }

    private AnimalCase find(Long id) {
        return cases.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found."));
    }

    private boolean isOwnedBy(AnimalCase animalCase, String email) {
        return animalCase.getAssignedClinic() != null && animalCase.getAssignedClinic().getOwner() != null
                && animalCase.getAssignedClinic().getOwner().getEmail().equalsIgnoreCase(email);
    }

    private void requireRole(String email, Role role) {
        if (user(email).getRole() != role) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action requires " + role + " access.");
    }

    private boolean allowed(CaseStatus current, CaseStatus next) {
        if (current == next) return false;
        return switch (current) {
            case REPORTED -> EnumSet.of(CaseStatus.UNDER_REVIEW, CaseStatus.ASSIGNED, CaseStatus.CANCELLED, CaseStatus.REJECTED).contains(next);
            case UNDER_REVIEW -> EnumSet.of(CaseStatus.ASSIGNED, CaseStatus.CANCELLED, CaseStatus.REJECTED).contains(next);
            case ASSIGNED -> EnumSet.of(CaseStatus.RESCUE_IN_PROGRESS, CaseStatus.AT_CLINIC, CaseStatus.CANCELLED).contains(next);
            case RESCUE_IN_PROGRESS -> EnumSet.of(CaseStatus.AT_CLINIC, CaseStatus.CANCELLED).contains(next);
            case AT_CLINIC -> EnumSet.of(CaseStatus.UNDER_TREATMENT, CaseStatus.COMPLETED).contains(next);
            case UNDER_TREATMENT -> next == CaseStatus.COMPLETED;
            case COMPLETED, CANCELLED, REJECTED -> false;
        };
    }
}