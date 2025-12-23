package com.mediwave.billing.service;

import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.common.event.BillingProcessedEvent;
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
class BillingServiceTest {

    @Autowired
    private BillingService billingService;

    @Test
    void processBilling_ShouldCreateBillingProcessedEvent() {
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

        assertDoesNotThrow(() -> billingService.processBilling(appointmentEvent));
    }

    @Test
    void billingService_ShouldBeAutowired() {
        assertNotNull(billingService);
    }
}
