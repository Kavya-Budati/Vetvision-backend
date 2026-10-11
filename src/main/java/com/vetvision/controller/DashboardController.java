package com.vetvision.controller;

import com.vetvision.entity.CaseStatus;
import com.vetvision.service.CaseService;
import com.vetvision.service.AppointmentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final CaseService cases;
    private final AppointmentService appointments;

    public DashboardController(CaseService cases, AppointmentService appointments) {
        this.cases = cases;
        this.appointments = appointments;
    }

    @GetMapping
    public Map<String, Object> statistics(Authentication auth) {
        var caseList = cases.list(auth.getName());
        Map<CaseStatus, Long> counts = caseList.stream().collect(Collectors.groupingBy(CaseResponse -> CaseResponse.status(),
                () -> new EnumMap<>(CaseStatus.class), Collectors.counting()));
        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("totalCases", caseList.size());
        response.put("reported", counts.getOrDefault(CaseStatus.REPORTED, 0L));
        response.put("active", caseList.stream().filter(c -> !java.util.Set.of(CaseStatus.COMPLETED, CaseStatus.CANCELLED, CaseStatus.REJECTED).contains(c.status())).count());
        response.put("underTreatment", counts.getOrDefault(CaseStatus.UNDER_TREATMENT, 0L));
        response.put("completed", counts.getOrDefault(CaseStatus.COMPLETED, 0L));
        response.put("upcomingAppointments", appointments.list(auth.getName()).stream()
                .filter(a -> a.appointmentDateTime().isAfter(java.time.LocalDateTime.now())
                        && a.status() != com.vetvision.entity.AppointmentStatus.CANCELLED
                        && a.status() != com.vetvision.entity.AppointmentStatus.COMPLETED).count());
        response.put("statusCounts", counts);
        return response;
    }
}