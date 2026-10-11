package com.vetvision.service;

import com.vetvision.dto.ClinicRequest;
import com.vetvision.dto.ClinicResponse;
import com.vetvision.entity.Role;
import com.vetvision.entity.VeterinaryClinic;
import com.vetvision.repository.UserRepository;
import com.vetvision.repository.VeterinaryClinicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClinicService {
    private final VeterinaryClinicRepository clinics;
    private final UserRepository users;
    private final CaseService cases;

    public ClinicService(VeterinaryClinicRepository clinics, UserRepository users, CaseService cases) {
        this.clinics = clinics;
        this.users = users;
        this.cases = cases;
    }

    @Transactional(readOnly = true)
    public List<ClinicResponse> list() {
        return clinics.findAllByOrderByNameAsc().stream().map(ClinicResponse::from).toList();
    }

    @Transactional
    public ClinicResponse create(ClinicRequest request, String currentEmail) {
        if (cases.user(currentEmail).getRole() != Role.ORGANIZATION) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only an organization can provision clinics.");
        if (clinics.existsByEmailIgnoreCase(request.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "A clinic with this email already exists.");
        VeterinaryClinic clinic = new VeterinaryClinic();
        clinic.setName(request.name().trim());
        clinic.setAddress(request.address().trim());
        clinic.setCity(request.city().trim());
        clinic.setPhone(request.phone().trim());
        clinic.setEmail(request.email().trim().toLowerCase());
        clinic.setServices(request.services());
        clinic.setOperatingHours(request.operatingHours());
        if (request.ownerEmail() != null && !request.ownerEmail().isBlank()) {
            var owner = users.findByEmailIgnoreCase(request.ownerEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic owner account was not found."));
            if (owner.getRole() != Role.CLINIC) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Clinic owner must have the CLINIC role provisioned by an administrator.");
            clinic.setOwner(owner);
        }
        return ClinicResponse.from(clinics.save(clinic));
    }
}