package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.dto.auth.InviteUserRequest;
import com.dynalar.dynalar.model.user.Dentist;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.DentistRepository;
import com.dynalar.dynalar.respository.UserRepository;
import com.dynalar.dynalar.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DentistRepository dentistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- NUEVO ENDPOINT PARA OBTENER TODO EL PERSONAL ---
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Ocultar contraseñas por seguridad
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @PostMapping("/invite-user") 
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createStaffUser(@RequestBody InviteUserRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("El correo ya está registrado.");
            }
            String cleanRole = request.getRole().replace("ROLE_", "");
            Role roleEnum = Role.valueOf(cleanRole); 

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
            boolean isOwner = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
            
            if (roleEnum == Role.SUPERADMIN && !isSuperAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Solo el SuperAdministrador puede crear otros SuperAdministradores.");
            }
            
            if (roleEnum == Role.OWNER && !isSuperAdmin && !isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Un Administrador normal no puede crear Propietarios.");
            }
            
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setSurname(request.getSurname());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(tempPassword));
            newUser.setDni(request.getDni());
            newUser.setPhone(request.getPhone());
            newUser.setSex(request.getSex());
            newUser.setRoles(Set.of(roleEnum));

            User savedUser = userRepository.save(newUser);
            
            if (roleEnum == Role.DOCTOR) {
                Dentist newDentist = new Dentist();
                newDentist.setUser(savedUser);
                dentistRepository.save(newDentist);
            }
            
            try {
                emailService.sendInitialPassword(savedUser.getEmail(), tempPassword);
                System.out.println("Email enviado a: " + savedUser.getEmail());
            } catch (Exception e) {
                System.err.println("Aviso: No se pudo entregar el email. Causa: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(java.util.Map.of("message", "Usuari creat correctament"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("El rol especificado no es válido.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario.");
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }
}