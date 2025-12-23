package com.mediwave.appointment.controller;

import com.mediwave.common.dto.AppointmentDTO;
import com.mediwave.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@Valid @RequestBody AppointmentDTO appointmentDTO) {
        log.info("Received request to create appointment for patient: {}", appointmentDTO.getPatientId());
        AppointmentDTO created = appointmentService.createAppointment(appointmentDTO);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        log.info("Received request to get all appointments");
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointment(@PathVariable Long id) {
        log.info("Received request to get appointment: {}", id);
        AppointmentDTO appointment = appointmentService.getAppointment(id);
        return ResponseEntity.ok(appointment);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam AppointmentDTO.AppointmentStatus status) {
        log.info("Received request to update appointment {} status to: {}", id, status);
        AppointmentDTO updated = appointmentService.updateAppointmentStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
