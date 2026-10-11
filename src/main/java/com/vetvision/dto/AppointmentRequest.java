package com.vetvision.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record AppointmentRequest(@NotNull Long caseId, @NotNull Long clinicId,
                                 @NotNull LocalDateTime appointmentDateTime, @Size(max = 2000) String notes) { }