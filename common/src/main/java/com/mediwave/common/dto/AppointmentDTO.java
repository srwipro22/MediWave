package com.mediwave.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    
    private Long id;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
    
    @NotBlank(message = "Appointment type is required")
    private String appointmentType;
    
    private String notes;
    
    private AppointmentStatus status;
    
    public enum AppointmentStatus {
        SCHEDULED,
        CONFIRMED,
        CANCELLED,
        BILLING_FAILED,
        RESOURCE_UNAVAILABLE,
        COMPLETED
    }
}
