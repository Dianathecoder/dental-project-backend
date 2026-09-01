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

    // Para que las clínicas orgánicas se den de alta
    @PostMapping("/register/clinic")
    public ResponseEntity<AuthResponse> registerClinic(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerClinicAdmin(req));
    }

    // Para pacientes
    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@RequestBody @Valid RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerPatientApp(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    // Para distinguir admin o patient cuando es una cuenta nueva
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin(
            @RequestBody GoogleAuthRequest req,
            @RequestParam(defaultValue = "patient") String type) throws Exception {
        return ResponseEntity.ok(authService.googleLogin(req.getIdToken(), type));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
        authService.forgotPassword(req.getEmail());
        return ResponseEntity.ok(new MessageResponse("Correo enviado correctamente"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        authService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Contraseña restablecida correctamente"));
    }
}