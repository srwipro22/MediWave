package com.mediwave.appointment.kafka;

import com.mediwave.common.event.BillingProcessedEvent;
import com.mediwave.common.event.ResourceAllocatedEvent;
import com.mediwave.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    
    private final AppointmentService appointmentService;
    
    @KafkaListener(topics = "billing-events", groupId = "appointment-service")
    public void handleBillingProcessedEvent(BillingProcessedEvent event) {
        log.info("Received BillingProcessedEvent: {}", event);
        appointmentService.handleBillingProcessedEvent(event);
    }
    
    @KafkaListener(topics = "resource-events", groupId = "appointment-service")
    public void handleResourceAllocatedEvent(ResourceAllocatedEvent event) {
        log.info("Received ResourceAllocatedEvent: {}", event);
        appointmentService.handleResourceAllocatedEvent(event);
    }
}
