package com.mediwave.billing.service;

import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.common.event.BillingProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();
    
    public void processBilling(AppointmentBookedEvent event) {
        log.info("Processing billing for appointment: {}", event.getAppointmentId());
        
        try {
            BillingProcessedEvent billingEvent = simulateBillingProcessing(event);
            kafkaTemplate.send("billing-events", billingEvent);
            
            log.info("Published BillingProcessedEvent for appointment: {}, status: {}", 
                    event.getAppointmentId(), billingEvent.getStatus());
        } catch (Exception e) {
            log.error("Error processing billing for appointment: {}", event.getAppointmentId(), e);
            
            BillingProcessedEvent failureEvent = BillingProcessedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .status(BillingProcessedEvent.BillingStatus.FAILURE)
                    .failureReason("Processing error: " + e.getMessage())
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
            
            kafkaTemplate.send("billing-events", failureEvent);
        }
    }
    
    private BillingProcessedEvent simulateBillingProcessing(AppointmentBookedEvent event) {
        String insuranceProvider = getRandomInsuranceProvider();
        BigDecimal totalAmount = calculateTotalAmount(event.getAppointmentType());
        BigDecimal coPayAmount = calculateCoPay(totalAmount, insuranceProvider);
        
        boolean billingSuccess = random.nextDouble() > 0.1;
        
        if (billingSuccess) {
            return BillingProcessedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .status(BillingProcessedEvent.BillingStatus.SUCCESS)
                    .amount(totalAmount)
                    .currency("USD")
                    .insuranceProvider(insuranceProvider)
                    .coPayAmount(coPayAmount)
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
        } else {
            return BillingProcessedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .status(BillingProcessedEvent.BillingStatus.FAILURE)
                    .failureReason("Insurance verification failed")
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
        }
    }
    
    private BigDecimal calculateTotalAmount(String appointmentType) {
        return switch (appointmentType.toLowerCase()) {
            case "general" -> new BigDecimal("150.00");
            case "specialist" -> new BigDecimal("250.00");
            case "emergency" -> new BigDecimal("500.00");
            case "consultation" -> new BigDecimal("100.00");
            default -> new BigDecimal("200.00");
        };
    }
    
    private BigDecimal calculateCoPay(BigDecimal totalAmount, String insuranceProvider) {
        BigDecimal coPayPercentage = switch (insuranceProvider) {
            case "BlueCross" -> new BigDecimal("0.20");
            case "Aetna" -> new BigDecimal("0.15");
            case "UnitedHealth" -> new BigDecimal("0.25");
            case "Medicare" -> new BigDecimal("0.10");
            default -> new BigDecimal("0.30");
        };
        
        return totalAmount.multiply(coPayPercentage).setScale(2, RoundingMode.HALF_UP);
    }
    
    private String getRandomInsuranceProvider() {
        String[] providers = {"BlueCross", "Aetna", "UnitedHealth", "Medicare", "Cigna"};
        return providers[random.nextInt(providers.length)];
    }
}
