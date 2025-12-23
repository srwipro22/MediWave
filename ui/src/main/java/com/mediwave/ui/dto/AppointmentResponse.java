package com.mediwave.ui.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String notes;
    private String status;
}
