package com.mediwave.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAllocatedEvent {
    
    private Long appointmentId;
    private Long doctorId;
    private Long roomId;
    private String equipmentType;
    private AllocationStatus status;
    private String failureReason;
    private LocalDateTime eventTimestamp;
    private String eventId;
    
    public enum AllocationStatus {
        SUCCESS,
        FAILURE
    }
}
