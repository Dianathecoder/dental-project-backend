package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.UserRepository;
import com.dynalar.dynalar.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Solo el ADMIN puede crear usuarios del sistema con roles específicos
    @PostMapping("/create-staff")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createStaffUser(@RequestBody User userRequest, @RequestParam Set<Role> roles) {
        try {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseEntity.badRequest().body("El correo ya está registrado.");
            }

            //Generar contraseña temporal limpia
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            //Configurar usuario
            User newUser = new User();
            newUser.setName(userRequest.getName());
            newUser.setSurname(userRequest.getSurname());
            newUser.setEmail(userRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(tempPassword)); // Encriptar para la BD
            newUser.setRoles(roles);

            User savedUser = userRepository.save(newUser);

            //Enviar correo con la contraseña en texto plano
            emailService.sendInitialPassword(savedUser.getEmail(), tempPassword);

            // Ocultar la contraseña en la respuesta HTTP
            savedUser.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario.");
        }
    }
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(org.springframework.security.core.Authentication authentication) {
        // authentication.getName() saca el email directamente del Token JWT
        String email = authentication.getName(); 
        
        java.util.Optional<User> user = userRepository.findByEmail(email);
        
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
    
}