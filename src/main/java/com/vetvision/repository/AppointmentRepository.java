package com.vetvision.repository;

import com.vetvision.entity.Appointment;
import com.vetvision.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByAppointmentDateTimeAsc();
    List<Appointment> findByAnimalCase_Reporter_EmailIgnoreCaseOrderByAppointmentDateTimeAsc(String email);
    List<Appointment> findByClinic_Owner_EmailIgnoreCaseOrderByAppointmentDateTimeAsc(String email);

    @Query("select count(a) > 0 from Appointment a where a.clinic.id = :clinicId and a.status <> :cancelled and a.appointmentDateTime < :endTime and a.appointmentDateTime > :startTime")
    boolean hasConflict(@Param("clinicId") Long clinicId, @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime, @Param("cancelled") AppointmentStatus cancelled);
}