package com.mediwave.resource.kafka;

import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.resource.service.ResourceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventConsumer {
    
    private final ResourceManagementService resourceManagementService;
    
    @KafkaListener(topics = "appointment-events", groupId = "resource-management-service")
    public void handleAppointmentBookedEvent(AppointmentBookedEvent event) {
        log.info("Received AppointmentBookedEvent: {}", event);
        resourceManagementService.allocateResources(event);
    }
}
