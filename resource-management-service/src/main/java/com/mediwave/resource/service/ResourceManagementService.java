package com.mediwave.resource.service;

import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.common.event.ResourceAllocatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceManagementService {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();
    
    public void allocateResources(AppointmentBookedEvent event) {
        log.info("Allocating resources for appointment: {}", event.getAppointmentId());
        
        try {
            ResourceAllocatedEvent resourceEvent = simulateResourceAllocation(event);
            kafkaTemplate.send("resource-events", resourceEvent);
            
            log.info("Published ResourceAllocatedEvent for appointment: {}, status: {}", 
                    event.getAppointmentId(), resourceEvent.getStatus());
        } catch (Exception e) {
            log.error("Error allocating resources for appointment: {}", event.getAppointmentId(), e);
            
            ResourceAllocatedEvent failureEvent = ResourceAllocatedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .doctorId(event.getDoctorId())
                    .status(ResourceAllocatedEvent.AllocationStatus.FAILURE)
                    .failureReason("Resource allocation error: " + e.getMessage())
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
            
            kafkaTemplate.send("resource-events", failureEvent);
        }
    }
    
    private ResourceAllocatedEvent simulateResourceAllocation(AppointmentBookedEvent event) {
        boolean doctorAvailable = checkDoctorAvailability(event.getDoctorId());
        boolean roomAvailable = checkRoomAvailability(event.getAppointmentType());
        boolean equipmentAvailable = checkEquipmentAvailability(event.getAppointmentType());
        
        if (doctorAvailable && roomAvailable && equipmentAvailable) {
            Long roomId = allocateRoom(event.getAppointmentType());
            String equipmentType = getRequiredEquipment(event.getAppointmentType());
            
            return ResourceAllocatedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .doctorId(event.getDoctorId())
                    .roomId(roomId)
                    .equipmentType(equipmentType)
                    .status(ResourceAllocatedEvent.AllocationStatus.SUCCESS)
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
        } else {
            String failureReason = determineFailureReason(doctorAvailable, roomAvailable, equipmentAvailable);
            
            return ResourceAllocatedEvent.builder()
                    .appointmentId(event.getAppointmentId())
                    .doctorId(event.getDoctorId())
                    .status(ResourceAllocatedEvent.AllocationStatus.FAILURE)
                    .failureReason(failureReason)
                    .eventTimestamp(LocalDateTime.now())
                    .eventId(UUID.randomUUID().toString())
                    .build();
        }
    }
    
    private boolean checkDoctorAvailability(Long doctorId) {
        return random.nextDouble() > 0.15;
    }
    
    private boolean checkRoomAvailability(String appointmentType) {
        double availabilityRate = switch (appointmentType.toLowerCase()) {
            case "emergency" -> 0.80;
            case "general" -> 0.90;
            case "specialist" -> 0.85;
            case "consultation" -> 0.95;
            default -> 0.85;
        };
        return random.nextDouble() < availabilityRate;
    }
    
    private boolean checkEquipmentAvailability(String appointmentType) {
        double availabilityRate = switch (appointmentType.toLowerCase()) {
            case "emergency" -> 0.90;
            case "general" -> 0.95;
            case "specialist" -> 0.88;
            case "consultation" -> 0.98;
            default -> 0.92;
        };
        return random.nextDouble() < availabilityRate;
    }
    
    private Long allocateRoom(String appointmentType) {
        return switch (appointmentType.toLowerCase()) {
            case "emergency" -> (long) (random.nextInt(5) + 1);
            case "general" -> (long) (random.nextInt(10) + 6);
            case "specialist" -> (long) (random.nextInt(8) + 11);
            case "consultation" -> (long) (random.nextInt(15) + 16);
            default -> (long) (random.nextInt(20) + 1);
        };
    }
    
    private String getRequiredEquipment(String appointmentType) {
        return switch (appointmentType.toLowerCase()) {
            case "emergency" -> "Emergency Kit, Defibrillator, Oxygen Tank";
            case "general" -> "Basic Medical Kit, Stethoscope, Blood Pressure Monitor";
            case "specialist" -> "Specialized Equipment, Diagnostic Tools";
            case "consultation" -> "Basic Examination Tools";
            default -> "Standard Medical Equipment";
        };
    }
    
    private String determineFailureReason(boolean doctorAvailable, boolean roomAvailable, boolean equipmentAvailable) {
        if (!doctorAvailable) {
            return "Doctor not available at requested time";
        }
        if (!roomAvailable) {
            return "No suitable room available";
        }
        if (!equipmentAvailable) {
            return "Required equipment not available";
        }
        return "Unknown resource allocation failure";
    }
}
