package com.vetvision.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_records")
@Getter
@Setter
public class TreatmentRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_case_id", nullable = false)
    private AnimalCase animalCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private VeterinaryClinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    private User veterinarian;

    @Column(nullable = false, length = 1500)
    private String diagnosis;

    @Column(nullable = false, length = 2000)
    private String treatment;

    @Column(length = 2000)
    private String careInstructions;

    @Column(nullable = false)
    private LocalDate treatmentDate;

    private LocalDate followUpDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}