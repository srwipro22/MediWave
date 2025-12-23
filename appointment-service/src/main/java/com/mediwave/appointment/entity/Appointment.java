package com.mediwave.appointment.entity;

import com.mediwave.common.dto.AppointmentDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long patientId;
    
    @Column(nullable = false)
    private Long doctorId;
    
    @Column(nullable = false)
    private LocalDateTime appointmentDate;
    
    @Column(nullable = false)
    private String appointmentType;
    
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentDTO.AppointmentStatus status;
    
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = AppointmentDTO.AppointmentStatus.SCHEDULED;
        }
    }
}
