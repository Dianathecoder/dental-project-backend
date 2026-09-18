package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.dto.auth.InviteUserRequest;
import com.dynalar.dynalar.model.Treatment;
import com.dynalar.dynalar.model.user.Dentist;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.DentistRepository;
import com.dynalar.dynalar.respository.TreatmentRepository;
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

import java.util.HashSet;
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
    private TreatmentRepository treatmentRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(null); 
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
        }
    }
    

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
            
            // --- BLOQUE MODIFICADO PARA ASIGNAR LOS TRATAMIENTOS ---
            if (roleEnum == Role.DOCTOR) {
                Dentist newDentist = new Dentist();
                newDentist.setUser(savedUser);
                
                // Si el frontend envía IDs de tratamientos, los buscamos y se los asignamos
                if (request.getTreatmentIds() != null && !request.getTreatmentIds().isEmpty()) {
                    List<Treatment> tratamientos = treatmentRepository.findAllById(request.getTreatmentIds());
                    newDentist.setTreatments(new HashSet<>(tratamientos));
                } else {
                    newDentist.setTreatments(new HashSet<>());
                }
                
                dentistRepository.save(newDentist);
            }
            // --------------------------------------------------------
            
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
    
    @PostMapping("/update-avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAvatar(@RequestBody java.util.Map<String, String> body) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Extraemos la URL o Path que nos manda Android
                String newAvatarUrl = body.get("avatarUrl");
                
                // Actualizamos el usuario y guardamos
                user.setAvatarUrl(newAvatarUrl);
                userRepository.save(user);
                
                return ResponseEntity.ok(java.util.Map.of("message", "Avatar actualizado correctamente"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar avatar.");
        }
    }
 // Añade este método en UserController.java
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 1. Si es Doctor, eliminamos su perfil de Dentista primero para que la BD no bloquee el borrado
                if (user.getRoles().contains(Role.DOCTOR)) {
                    Optional<Dentist> dentistOpt = dentistRepository.findByUserId(id);
                    dentistOpt.ifPresent(dentist -> dentistRepository.delete(dentist));
                }

                // 2. Ahora sí podemos borrar al usuario de forma segura
                userRepository.delete(user);
                
                return ResponseEntity.ok(java.util.Map.of("message", "Usuari eliminat correctament"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar usuari.");
        }
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody InviteUserRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                user.setName(request.getName());
                user.setSurname(request.getSurname());
                user.setEmail(request.getEmail());
                user.setDni(request.getDni());
                user.setPhone(request.getPhone());
                user.setSex(request.getSex());
                
                String cleanRole = request.getRole().replace("ROLE_", "");
                Role roleEnum = Role.valueOf(cleanRole);
                user.setRoles(Set.of(roleEnum));
                
                userRepository.save(user);

                // Si cambiaron el rol a DOCTOR y no tenía perfil de Dentista, se lo creamos
                if (roleEnum == Role.DOCTOR && dentistRepository.findByUserId(id).isEmpty()) {
                    Dentist newDentist = new Dentist();
                    newDentist.setUser(user);
                    dentistRepository.save(newDentist);
                }

                return ResponseEntity.ok(java.util.Map.of("message", "Usuari actualitzat"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuari no trobat");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualitzar");
        }
    }
    
}