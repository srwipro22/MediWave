package com.mediwave.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingProcessedEvent {
    
    private Long appointmentId;
    private BillingStatus status;
    private BigDecimal amount;
    private String currency;
    private String insuranceProvider;
    private BigDecimal coPayAmount;
    private String failureReason;
    private LocalDateTime eventTimestamp;
    private String eventId;
    
    public enum BillingStatus {
        SUCCESS,
        FAILURE
    }
}
