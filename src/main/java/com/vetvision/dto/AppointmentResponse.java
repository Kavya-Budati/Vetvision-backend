package com.vetvision.dto;

import com.vetvision.entity.Appointment;
import com.vetvision.entity.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(Long id, String caseId, String animalType, Long clinicId, String clinicName,
                                  LocalDateTime appointmentDateTime, AppointmentStatus status, String notes) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(appointment.getId(), appointment.getAnimalCase().getCaseId(),
                appointment.getAnimalCase().getAnimalType(), appointment.getClinic().getId(),
                appointment.getClinic().getName(), appointment.getAppointmentDateTime(), appointment.getStatus(),
                appointment.getNotes());
    }
}