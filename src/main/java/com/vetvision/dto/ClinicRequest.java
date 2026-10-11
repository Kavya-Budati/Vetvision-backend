package com.vetvision.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClinicRequest(@NotBlank @Size(max = 150) String name, @NotBlank @Size(max = 255) String address,
                            @NotBlank @Size(max = 100) String city, @NotBlank @Size(max = 40) String phone,
                            @Email @NotBlank String email, @Size(max = 2000) String services,
                            @Size(max = 255) String operatingHours, @Email String ownerEmail) { }