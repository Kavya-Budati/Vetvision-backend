package com.vetvision.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CaseRequest(
        @NotBlank @Size(max = 80) String animalType,
        @Size(max = 100) String breed,
        @NotBlank @Size(max = 2000) String description,
        @Size(max = 2000) String symptoms,
        @NotBlank @Size(max = 255) String location,
        @NotBlank String urgency) { }