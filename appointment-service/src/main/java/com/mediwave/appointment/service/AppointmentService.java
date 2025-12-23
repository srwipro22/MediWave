package com.mediwave.appointment.service;

import com.mediwave.appointment.entity.Appointment;
import com.mediwave.appointment.repository.AppointmentRepository;
import com.mediwave.common.dto.AppointmentDTO;
import com.mediwave.common.event.AppointmentBookedEvent;
import com.mediwave.common.event.BillingProcessedEvent;
import com.mediwave.common.event.ResourceAllocatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        log.info("Creating appointment for patient: {}", appointmentDTO.getPatientId());
        
        Appointment appointment = Appointment.builder()
                .patientId(appointmentDTO.getPatientId())
                .doctorId(appointmentDTO.getDoctorId())
                .appointmentDate(appointmentDTO.getAppointmentDate())
                .appointmentType(appointmentDTO.getAppointmentType())
                .notes(appointmentDTO.getNotes())
                .status(AppointmentDTO.AppointmentStatus.SCHEDULED)
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        AppointmentBookedEvent event = AppointmentBookedEvent.builder()
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentType(appointment.getAppointmentType())
                .notes(appointment.getNotes())
                .eventTimestamp(LocalDateTime.now())
                .eventId(UUID.randomUUID().toString())
                .build();
        
        kafkaTemplate.send("appointment-events", event);
        log.info("Published AppointmentBookedEvent for appointment: {}", appointment.getId());
        
        return convertToDTO(appointment);
    }
    
    @Transactional
    public List<AppointmentDTO> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional
    public AppointmentDTO getAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        return convertToDTO(appointment);
    }
    
    @Transactional
    public AppointmentDTO updateAppointmentStatus(Long id, AppointmentDTO.AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + id));
        
        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);
        
        log.info("Updated appointment {} status to: {}", id, status);
        return convertToDTO(appointment);
    }
    
    @Transactional
    public void handleBillingProcessedEvent(BillingProcessedEvent event) {
        log.info("Handling BillingProcessedEvent for appointment: {}", event.getAppointmentId());
        
        Appointment appointment = appointmentRepository.findById(event.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + event.getAppointmentId()));
        
        if (event.getStatus() == BillingProcessedEvent.BillingStatus.SUCCESS) {
            if (appointment.getStatus() == AppointmentDTO.AppointmentStatus.SCHEDULED) {
                appointment.setStatus(AppointmentDTO.AppointmentStatus.CONFIRMED);
            }
        } else {
            appointment.setStatus(AppointmentDTO.AppointmentStatus.BILLING_FAILED);
        }
        
        appointmentRepository.save(appointment);
        log.info("Updated appointment {} status after billing processing: {}", 
                event.getAppointmentId(), appointment.getStatus());
    }
    
    @Transactional
    public void handleResourceAllocatedEvent(ResourceAllocatedEvent event) {
        log.info("Handling ResourceAllocatedEvent for appointment: {}", event.getAppointmentId());
        
        Appointment appointment = appointmentRepository.findById(event.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + event.getAppointmentId()));
        
        if (event.getStatus() == ResourceAllocatedEvent.AllocationStatus.SUCCESS) {
            if (appointment.getStatus() == AppointmentDTO.AppointmentStatus.SCHEDULED) {
                appointment.setStatus(AppointmentDTO.AppointmentStatus.CONFIRMED);
            }
        } else {
            appointment.setStatus(AppointmentDTO.AppointmentStatus.RESOURCE_UNAVAILABLE);
        }
        
        appointmentRepository.save(appointment);
        log.info("Updated appointment {} status after resource allocation: {}", 
                event.getAppointmentId(), appointment.getStatus());
    }
    
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentType(appointment.getAppointmentType())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .build();
    }
}
