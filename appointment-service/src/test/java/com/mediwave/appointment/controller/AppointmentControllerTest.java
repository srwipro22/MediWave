package com.mediwave.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediwave.appointment.service.AppointmentService;
import com.mediwave.common.dto.AppointmentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAppointment_ShouldReturnCreatedAppointment() throws Exception {
        AppointmentDTO request = AppointmentDTO.builder()
                .patientId(123L)
                .doctorId(456L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .appointmentType("general")
                .notes("Regular checkup")
                .build();

        AppointmentDTO response = AppointmentDTO.builder()
                .id(1L)
                .patientId(123L)
                .doctorId(456L)
                .appointmentDate(request.getAppointmentDate())
                .appointmentType("general")
                .notes("Regular checkup")
                .status(AppointmentDTO.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(response);

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(123))
                .andExpect(jsonPath("$.doctorId").value(456))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void getAppointment_ShouldReturnAppointment() throws Exception {
        AppointmentDTO appointment = AppointmentDTO.builder()
                .id(1L)
                .patientId(123L)
                .doctorId(456L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .appointmentType("general")
                .status(AppointmentDTO.AppointmentStatus.SCHEDULED)
                .build();

        when(appointmentService.getAppointment(1L)).thenReturn(appointment);

        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(123));
    }

    @Test
    void updateAppointmentStatus_ShouldReturnUpdatedAppointment() throws Exception {
        AppointmentDTO updated = AppointmentDTO.builder()
                .id(1L)
                .patientId(123L)
                .doctorId(456L)
                .appointmentDate(LocalDateTime.now().plusDays(1))
                .appointmentType("general")
                .status(AppointmentDTO.AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentService.updateAppointmentStatus(1L, AppointmentDTO.AppointmentStatus.CONFIRMED))
                .thenReturn(updated);

        mockMvc.perform(put("/appointments/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
