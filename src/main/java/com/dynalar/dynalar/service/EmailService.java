package com.dynalar.dynalar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Email obligatorio para Staff (Doctor / Auxiliar)
    public void sendInitialPassword(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenido a Dynalar - Tus credenciales de acceso");
        message.setText("Hola,\n\nSe ha creado tu cuenta de usuario en la clínica Dynalar.\n\n"
                + "Tus credenciales para acceder son:\n"
                + "Correo: " + toEmail + "\n"
                + "Contraseña temporal: " + tempPassword + "\n\n"
                + "Por motivos de seguridad, te recomendamos cambiar esta contraseña al iniciar sesión.");

        mailSender.send(message);
    }

    // Email opcional para Pacientes (Invitación a la App)
    public void sendPatientAppInvitation(String toEmail, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Bienvenido a Dynalar - Acceso a tu App de Salud");
        message.setText("Hola,\n\nSe ha creado tu ficha en la clínica Dynalar.\n\n"
                + "Si deseas gestionar tus citas, consultar tu historial o hablar por chat con nosotros, "
                + "puedes descargarte nuestra App e iniciar sesión con las siguientes credenciales:\n\n"
                + "Usuario: " + toEmail + "\n"
                + "Contraseña temporal: " + tempPassword + "\n\n"
                + "Recuerda que también puedes recibir la información de tus citas directamente por WhatsApp.");

        mailSender.send(message);
    }
}