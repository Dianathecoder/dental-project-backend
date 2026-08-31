package com.dynalar.dynalar.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dynalar.dynalar.model.Appointment;
import com.dynalar.dynalar.model.patient.Patient;

@Service
public class WhatsAppService {


    @Value("${whatsapp.api.url:}")
    private String apiUrl;
    
    @Value("${whatsapp.api.token:}")
    private String apiToken;

    public void sendAppointmentNotification(Patient patient, Appointment appointment) {
        if (patient.getPhone() == null || patient.getPhone().isEmpty()) {
            return; 
        }

        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getStartTime().toLocalDate();
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String timeStr = appointment.getStartTime().format(timeFormatter);
        String dateStr = appointmentDate.format(dateFormatter);
        
        String message;

        
        if (appointmentDate.equals(today)) {
            message = "Hola " + patient.getName() + ", te confirmamos tu cita para *HOY a las " + timeStr + "*. \n\n"
                    + "⚠️ Al ser una cita para el mismo día, si necesitas modificarla o tienes alguna urgencia, "
                    + "por favor escríbenos directamente por el chat de la aplicación o llámanos a la clínica.";
        } else {
            message = "Hola " + patient.getName() + ", tu cita en la clínica ha sido agendada para el *" 
                    + dateStr + " a las " + timeStr + "*. \n\n"
                    + "Puedes gestionar tu cita desde la aplicación.";
        }

        sendToWhatsAppApi(patient.getPhone(), message);
    }

    private void sendToWhatsAppApi(String phoneNumber, String message) {
        // Aquí va la llamada HTTP a tu proveedor de WhatsApp (Meta, Twilio, etc.)
        // Ejemplo genérico usando RestTemplate:
        
        /*
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("phone", phoneNumber); // Asegúrate de que incluya el prefijo del país (ej. +34)
        body.put("message", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        
        try {
            restTemplate.postForEntity(apiUrl, request, String.class);
        } catch (Exception e) {
            System.err.println("Error enviando WhatsApp: " + e.getMessage());
        }
        */
        
        System.out.println("Simulando envío de WhatsApp a " + phoneNumber + ":\n" + message);
    }
}