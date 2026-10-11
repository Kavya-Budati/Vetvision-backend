package com.vetvision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TreatmentRequest(@NotNull Long caseId, @NotBlank @Size(max = 1500) String diagnosis,
                               @NotBlank @Size(max = 2000) String treatment,
                               @Size(max = 2000) String careInstructions,
                               @NotNull LocalDate treatmentDate, LocalDate followUpDate) { }