package com.vetvision.dto;

import com.vetvision.entity.VeterinaryClinic;

public record ClinicResponse(Long id, String name, String address, String city, String phone, String email,
                             String services, String operatingHours) {
    public static ClinicResponse from(VeterinaryClinic clinic) {
        return new ClinicResponse(clinic.getId(), clinic.getName(), clinic.getAddress(), clinic.getCity(),
                clinic.getPhone(), clinic.getEmail(), clinic.getServices(), clinic.getOperatingHours());
    }
}