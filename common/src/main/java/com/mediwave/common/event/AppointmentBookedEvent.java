package com.mediwave.common.event;

import com.mediwave.common.dto.AppointmentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookedEvent {
    
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String appointmentType;
    private String notes;
    private LocalDateTime eventTimestamp;
    private String eventId;
}
