package com.mediwave.ui.controller;

import com.mediwave.ui.dto.AppointmentRequest;
import com.mediwave.ui.dto.AppointmentResponse;
import com.mediwave.ui.dto.AppointmentStatusUpdate;
import com.mediwave.ui.service.AppointmentServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {
    
    private final AppointmentServiceClient appointmentServiceClient;
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appointmentRequest", new AppointmentRequest());
        return "index";
    }
    
    @GetMapping("/appointments")
    public String listAppointments(Model model) {
        try {
            List<AppointmentResponse> appointments = appointmentServiceClient.getAllAppointments();
            model.addAttribute("appointments", appointments);
            return "appointments";
        } catch (Exception e) {
            log.error("Error fetching appointments", e);
            model.addAttribute("error", "Failed to fetch appointments: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/appointments")
    public String createAppointment(@ModelAttribute AppointmentRequest appointmentRequest, 
                                   RedirectAttributes redirectAttributes) {
        try {
            AppointmentResponse created = appointmentServiceClient.createAppointment(appointmentRequest);
            redirectAttributes.addFlashAttribute("success", 
                    "Appointment created successfully! ID: " + created.getId());
            return "redirect:/appointments";
        } catch (Exception e) {
            log.error("Error creating appointment", e);
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to create appointment: " + e.getMessage());
            return "redirect:/";
        }
    }
    
    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable Long id, 
                                        @RequestParam String status,
                                        RedirectAttributes redirectAttributes) {
        try {
            AppointmentStatusUpdate statusUpdate = new AppointmentStatusUpdate();
            statusUpdate.setStatus(status);
            
            appointmentServiceClient.updateAppointmentStatus(id, statusUpdate);
            redirectAttributes.addFlashAttribute("success", 
                    "Appointment status updated successfully!");
            return "redirect:/appointments";
        } catch (Exception e) {
            log.error("Error updating appointment status", e);
            redirectAttributes.addFlashAttribute("error", 
                    "Failed to update appointment status: " + e.getMessage());
            return "redirect:/appointments";
        }
    }
    
    @GetMapping("/appointments/{id}")
    public String viewAppointment(@PathVariable Long id, Model model) {
        try {
            AppointmentResponse appointment = appointmentServiceClient.getAppointment(id);
            model.addAttribute("appointment", appointment);
            return "appointment-detail";
        } catch (Exception e) {
            log.error("Error fetching appointment", e);
            model.addAttribute("error", "Failed to fetch appointment: " + e.getMessage());
            return "error";
        }
    }
}
