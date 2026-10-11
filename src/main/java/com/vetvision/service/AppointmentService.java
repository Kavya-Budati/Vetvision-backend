package com.vetvision.service;

import com.vetvision.dto.AppointmentRequest;
import com.vetvision.dto.AppointmentResponse;
import com.vetvision.entity.Appointment;
import com.vetvision.entity.AppointmentStatus;
import com.vetvision.entity.Role;
import com.vetvision.entity.VeterinaryClinic;
import com.vetvision.repository.AnimalCaseRepository;
import com.vetvision.repository.AppointmentRepository;
import com.vetvision.repository.VeterinaryClinicRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final AnimalCaseRepository cases;
    private final VeterinaryClinicRepository clinics;
    private final CaseService caseService;

    public AppointmentService(AppointmentRepository appointments, AnimalCaseRepository cases,
                              VeterinaryClinicRepository clinics, CaseService caseService) {
        this.appointments = appointments;
        this.cases = cases;
        this.clinics = clinics;
        this.caseService = caseService;
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> list(String email) {
        Role role = caseService.user(email).getRole();
        List<Appointment> result = switch (role) {
            case ORGANIZATION -> appointments.findAllByOrderByAppointmentDateTimeAsc();
            case CLINIC -> appointments.findByClinic_Owner_EmailIgnoreCaseOrderByAppointmentDateTimeAsc(email);
            case RESCUER -> appointments.findByAnimalCase_Reporter_EmailIgnoreCaseOrderByAppointmentDateTimeAsc(email);
        };
        return result.stream().map(AppointmentResponse::from).toList();
    }

    @Transactional
    public AppointmentResponse create(AppointmentRequest request, String email) {
        var animalCase = caseService.accessibleCase(request.caseId(), email);
        VeterinaryClinic clinic = clinics.findById(request.clinicId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Clinic not found."));
        boolean org = caseService.user(email).getRole() == Role.ORGANIZATION;
        if (!org && (animalCase.getAssignedClinic() == null || !animalCase.getAssignedClinic().getId().equals(clinic.getId()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The case must be assigned to this clinic before booking.");
        }
        LocalDateTime date = request.appointmentDateTime();
        if (!date.isAfter(LocalDateTime.now())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment must be in the future.");
        if (appointments.hasConflict(clinic.getId(), date.minusMinutes(30), date.plusMinutes(30), AppointmentStatus.CANCELLED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This clinic already has an appointment within 30 minutes of that time.");
        }
        Appointment appointment = new Appointment();
        appointment.setAnimalCase(animalCase);
        appointment.setClinic(clinic);
        appointment.setAppointmentDateTime(date);
        appointment.setNotes(request.notes());
        appointment.setCreatedBy(caseService.user(email));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        return AppointmentResponse.from(appointments.save(appointment));
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus next, String email) {
        Appointment appointment = appointments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));
        var user = caseService.user(email);
        boolean clinic = user.getRole() == Role.CLINIC && appointment.getClinic().getOwner() != null
                && appointment.getClinic().getOwner().getId().equals(user.getId());
        boolean org = user.getRole() == Role.ORGANIZATION;
        boolean reporterCancel = user.getRole() == Role.RESCUER
                && appointment.getAnimalCase().getReporter().getId().equals(user.getId())
                && next == AppointmentStatus.CANCELLED;
        if (!org && !clinic && !reporterCancel) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot manage this appointment.");
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == next) throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment is already closed or unchanged.");
        appointment.setStatus(next);
        return AppointmentResponse.from(appointments.save(appointment));
    }
}