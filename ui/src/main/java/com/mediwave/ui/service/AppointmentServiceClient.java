package com.mediwave.ui.service;

import com.mediwave.ui.dto.AppointmentRequest;
import com.mediwave.ui.dto.AppointmentResponse;
import com.mediwave.ui.dto.AppointmentStatusUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceClient {
    
    private final WebClient webClient;
    
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment: {}", request);
        
        return webClient.post()
                .uri("/appointments")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AppointmentResponse.class)
                .block();
    }
    
    public AppointmentResponse getAppointment(Long id) {
        log.info("Fetching appointment: {}", id);
        
        return webClient.get()
                .uri("/appointments/{id}", id)
                .retrieve()
                .bodyToMono(AppointmentResponse.class)
                .block();
    }
    
    public List<AppointmentResponse> getAllAppointments() {
        log.info("Fetching all appointments");
        
        Flux<AppointmentResponse> appointmentFlux = webClient.get()
                .uri("/appointments")
                .retrieve()
                .bodyToFlux(AppointmentResponse.class);
        
        return appointmentFlux.collectList().block();
    }
    
    public void updateAppointmentStatus(Long id, AppointmentStatusUpdate statusUpdate) {
        log.info("Updating appointment {} status to: {}", id, statusUpdate.getStatus());
        
        webClient.put()
                .uri("/appointments/{id}/status?status={status}", id, statusUpdate.getStatus())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
