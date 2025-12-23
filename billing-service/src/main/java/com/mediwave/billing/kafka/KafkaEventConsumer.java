package com.mediwave.billing.kafka;

import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    
    private final BillingService billingService;
    
    @KafkaListener(topics = "appointment-events", groupId = "billing-service")
    public void handleAppointmentBookedEvent(AppointmentBookedEvent event) {
        log.info("Received AppointmentBookedEvent: {}", event);
        billingService.processBilling(event);
    }
}
