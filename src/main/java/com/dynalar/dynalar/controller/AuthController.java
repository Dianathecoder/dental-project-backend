package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.dto.ResetPasswordRequest;
import com.dynalar.dynalar.dto.auth.*;
import com.dynalar.dynalar.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //Registro aadmin
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerClinic(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerClinicAdmin(req));
    }

    //Para Admins, Doctores, Auxiliares y Pacientes ya activados
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    //El backend decide automáticamente el rol (Admin nuevo, o Paciente invitado)
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleAuthRequest req) throws Exception {
    	return ResponseEntity.ok(authService.googleLogin(req.getIdToken()));
    }

    //Cuando pulsan el enlace del correo de invitación
    @PostMapping("/activate-patient")
    public ResponseEntity<MessageResponse> activatePatient(@RequestBody @Valid ResetPasswordRequest req) {
        authService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Cuenta de paciente activada y contraseña guardada."));
    }

    //Para recuperar la contraseña
    
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
        authService.forgotPassword(req.getEmail());
        return ResponseEntity.ok(new MessageResponse("Correo enviado correctamente"));
    }

    //Cambio de contraseña
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        authService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Contraseña restablecida correctamente"));
    }
}