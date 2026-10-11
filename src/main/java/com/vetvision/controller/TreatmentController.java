package com.vetvision.controller;

import com.vetvision.dto.TreatmentRequest;
import com.vetvision.dto.TreatmentResponse;
import com.vetvision.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TreatmentController {
    private final TreatmentService treatments;

    public TreatmentController(TreatmentService treatments) { this.treatments = treatments; }

    @GetMapping("/cases/{caseId}/treatments")
    public List<TreatmentResponse> list(@PathVariable Long caseId, Authentication auth) {
        return treatments.list(caseId, auth.getName());
    }

    @PostMapping("/treatments")
    @ResponseStatus(HttpStatus.CREATED)
    public TreatmentResponse create(@Valid @RequestBody TreatmentRequest request, Authentication auth) {
        return treatments.create(request, auth.getName());
    }
}