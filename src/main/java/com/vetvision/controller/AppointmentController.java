package com.vetvision.controller;

import com.vetvision.dto.AppointmentRequest;
import com.vetvision.dto.AppointmentResponse;
import com.vetvision.dto.AppointmentStatusRequest;
import com.vetvision.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointments;

    public AppointmentController(AppointmentService appointments) { this.appointments = appointments; }

    @GetMapping
    public List<AppointmentResponse> list(Authentication auth) { return appointments.list(auth.getName()); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest request, Authentication auth) {
        return appointments.create(request, auth.getName());
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody AppointmentStatusRequest request, Authentication auth) {
        return appointments.updateStatus(id, request.status(), auth.getName());
    }
}