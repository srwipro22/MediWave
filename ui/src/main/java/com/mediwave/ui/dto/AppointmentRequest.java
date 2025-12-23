package com.mediwave.ui.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    
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
}
