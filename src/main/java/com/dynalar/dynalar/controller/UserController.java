package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.dto.auth.InviteUserRequest;
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
    @PostMapping("/invite-user") 
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createStaffUser(@RequestBody InviteUserRequest request) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body("El correo ya está registrado.");
            }

            //Generar contraseña temporal limpia
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);

            //. Configurar usuario
            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setSurname(request.getSurname());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(passwordEncoder.encode(tempPassword));
                        newUser.setDni(request.getDni());
            newUser.setPhone(request.getPhone());
            newUser.setSex(request.getSex());

          
            Role roleEnum = Role.valueOf(request.getRole()); 
            newUser.setRoles(Set.of(roleEnum));

            User savedUser = userRepository.save(newUser);

            // 3. Enviar correo con la contraseña en texto plano
            emailService.sendInitialPassword(savedUser.getEmail(), tempPassword);

            // Ocultar la contraseña en la respuesta HTTP
            savedUser.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("El rol especificado no es válido.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el usuario.");
        }
    }
}