package com.vetvision.controller;

import com.vetvision.dto.CaseRequest;
import com.vetvision.dto.CaseResponse;
import com.vetvision.dto.CaseStatusRequest;
import com.vetvision.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseController {
    private final CaseService cases;

    public CaseController(CaseService cases) { this.cases = cases; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseResponse create(@Valid @RequestBody CaseRequest request, Authentication auth) {
        return cases.create(request, auth.getName());
    }

    @GetMapping
    public List<CaseResponse> list(Authentication auth) { return cases.list(auth.getName()); }

    @GetMapping("/{id}")
    public CaseResponse get(@PathVariable Long id, Authentication auth) { return cases.get(id, auth.getName()); }

    @PatchMapping("/{id}/status")
    public CaseResponse updateStatus(@PathVariable Long id, @Valid @RequestBody CaseStatusRequest request, Authentication auth) {
        return cases.updateStatus(id, request.status(), auth.getName());
    }

    @PutMapping("/{id}/assignment")
    public CaseResponse assign(@PathVariable Long id, @RequestParam Long clinicId, Authentication auth) {
        return cases.assign(id, clinicId, auth.getName());
    }
}