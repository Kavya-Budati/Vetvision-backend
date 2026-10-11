package com.vetvision.controller;

import com.vetvision.dto.ClinicRequest;
import com.vetvision.dto.ClinicResponse;
import com.vetvision.service.ClinicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {
    private final ClinicService clinics;

    public ClinicController(ClinicService clinics) { this.clinics = clinics; }

    @GetMapping
    public List<ClinicResponse> list() { return clinics.list(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClinicResponse create(@Valid @RequestBody ClinicRequest request, Authentication auth) {
        return clinics.create(request, auth.getName());
    }
}