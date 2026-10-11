package com.vetvision.repository;

import com.vetvision.entity.VeterinaryClinic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeterinaryClinicRepository extends JpaRepository<VeterinaryClinic, Long> {
    List<VeterinaryClinic> findAllByOrderByNameAsc();
    boolean existsByEmailIgnoreCase(String email);
}