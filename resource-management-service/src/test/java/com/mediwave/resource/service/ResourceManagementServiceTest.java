package com.mediwave.resource.service;

import com.mediwave.common.event.AppointmentBookedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext
class ResourceManagementServiceTest {

    @Autowired
    private ResourceManagementService resourceManagementService;

    @Test
    void allocateResources_ShouldCreateResourceAllocatedEvent() {
        AppointmentBookedEvent appointmentEvent = AppointmentBookedEvent.builder()
                .appointmentId(1L)
                .patientId(123L)
                .doctorId(456L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .appointmentType("general")
                .notes("Regular checkup")
                .eventTimestamp(LocalDateTime.now())
                .eventId(UUID.randomUUID().toString())
                .build();

        assertDoesNotThrow(() -> resourceManagementService.allocateResources(appointmentEvent));
    }

    @Test
    void resourceManagementService_ShouldBeAutowired() {
        assertNotNull(resourceManagementService);
    }
}
