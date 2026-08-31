package com.dynalar.dynalar.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.dynalar.dynalar.model.Appointment;
import com.dynalar.dynalar.model.patient.Patient;

@Service
public class WhatsAppService {

    public void sendAppointmentNotification(Patient patient, Appointment appointment) {
        // Verificar que el paciente tiene un teléfono registrado
        if (patient == null || patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
            System.out.println("⚠️ No se pudo enviar WhatsApp: El paciente no tiene teléfono registrado.");
            return;
        }

        // Extraer fechas y formatearlas
        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getStartTime().toLocalDate();
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        String timeStr = appointment.getStartTime().format(timeFormatter);
        String dateStr = appointmentDate.format(dateFormatter);
        
        String message;

      
        if (appointmentDate.equals(today)) {
            message = "Hola " + patient.getName() + ",\n\n"
                    + "Te confirmamos tu cita para *HOY a las " + timeStr + "*.\n\n"
                    + "⚠️ Al ser una cita para hoy o de urgencia, si necesitas modificarla o tienes algún imprevisto, "
                    + "por favor escríbenos directamente por el chat de la aplicación o llámanos a la clínica.";
        } else {
            message = "Hola " + patient.getName() + ",\n\n"
                    + "Tu cita en la clínica ha sido agendada para el *" + dateStr + " a las " + timeStr + "*.\n\n"
                    + "Recuerda que puedes gestionar tu cita o contactarnos a través del chat de la aplicación.";
        }

        
        sendToWhatsAppApi(patient.getPhone(), message);
    }

    private void sendToWhatsAppApi(String phoneNumber, String message) {
       
        System.out.println("\n========== 🟢 SIMULACIÓN WHATSAPP ENVIADO 🟢 ==========");
        System.out.println("Destinatario: " + phoneNumber);
        System.out.println("Mensaje:\n" + message);
        System.out.println("=========================================================\n");
    }
}