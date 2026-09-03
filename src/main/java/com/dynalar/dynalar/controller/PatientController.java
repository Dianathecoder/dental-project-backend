package com.dynalar.dynalar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.UUID;

import com.dynalar.dynalar.model.odontogram.Odontogram;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.PatientRepository;
import com.dynalar.dynalar.respository.UserRepository;
import com.dynalar.dynalar.service.EmailService;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    // Solo Admin, Auxiliar o Doctor pueden ver la lista general
    @GetMapping("/index")
    @PreAuthorize("@userSecurity.isStaffOrDoctor(authentication)")
    public ResponseEntity<?> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String initial) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending().and(Sort.by("lastName").ascending()).and(Sort.by("id").ascending()));
            
            if (initial != null && !initial.isEmpty()) {
                return ResponseEntity.ok(patientRepository.findByNameStartingWithIgnoreCase(initial, pageable));
            }
            
            return ResponseEntity.ok(patientRepository.findAll(pageable));
        } catch (Exception e) {
            // AQUI CAPTURAMOS EL ERROR Y LO ENVIAMOS AL FRONTEND
            return ResponseEntity.status(500).body("ERROR REAL DEL SERVIDOR: " + e.getMessage() + " | CAUSA: " + (e.getCause() != null ? e.getCause().getMessage() : "Desconocida"));
        }
    }
    
    // Solo el staff administrativo crea pacientes manualmente
    @PostMapping
    @PreAuthorize("@userSecurity.isStaff(authentication)")
    public ResponseEntity<?> createPatient(
            @RequestBody Patient patient,
            @RequestParam(defaultValue = "false") boolean createAppAccount) {
        try {
            // Si se solicita crear cuenta de App y el paciente tiene email
            if (createAppAccount && patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
                
                if (userRepository.existsByEmail(patient.getEmail())) {
                    return ResponseEntity.badRequest().body("Ya existe una cuenta de usuario con este correo.");
                }

                String tempPassword = UUID.randomUUID().toString().substring(0, 8);

                User newUser = new User();
                newUser.setName(patient.getName());
                newUser.setSurname(patient.getLastName());
                newUser.setEmail(patient.getEmail());
                newUser.setPassword(passwordEncoder.encode(tempPassword));
                newUser.getRoles().add(Role.PATIENT);

                User savedUser = userRepository.save(newUser);
                patient.setUser(savedUser);

                // Enviar correo de invitación a la App
                emailService.sendPatientAppInvitation(patient.getEmail(), tempPassword);
            }

            // Vincular registros médicos
            if (patient.getMedicalRecord() != null) {
                patient.getMedicalRecord().setPatient(patient);
            }

            if (patient.getOdontogram() == null) {
                Odontogram o = new Odontogram();
                o.setPatient(patient);
                patient.setOdontogram(o);
            } else {
                patient.getOdontogram().setPatient(patient);
            }

            //Guardar Ficha del Paciente
            Patient savedPatient = patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Todos los usuarios pueden ver su propio perfil
    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isSelfOrStaffOrDoctor(authentication, #id)")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        try {
            Patient patient = patientRepository.findById(id).orElse(null);
            if (patient == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
    
    // Todos los usuarios menos el Pacientes pueden buscar en la lista de pacientes
    @GetMapping("/search")
    @PreAuthorize("@userSecurity.isStaffOrDoctor(authentication)")
    public ResponseEntity<Page<Patient>> searchPatients(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending().and(Sort.by("lastName").ascending()).and(Sort.by("id").ascending()));	        
            Page<Patient> patients = patientRepository.searchPatientsAdvanced(query, pageable);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
  
    @PutMapping("/update")
    @PreAuthorize("@userSecurity.isSelfOrStaffOrDoctor(authentication, #updatedPatient.id)")
    public ResponseEntity<Patient> updatePatient(@RequestBody Patient updatedPatient) {
        try {
            Long id = updatedPatient.getId();
            if (id == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Patient existingPatient = patientRepository.findById(id).orElse(null);
            if (existingPatient == null) {
                return ResponseEntity.notFound().build();
            }

            if (updatedPatient.getUser() != null && existingPatient.getUser() != null) {
                User existingUser = existingPatient.getUser();
                User incomingUser = updatedPatient.getUser();

                if (incomingUser.getName() != null) existingUser.setName(incomingUser.getName());
                if (incomingUser.getSurname() != null) existingUser.setSurname(incomingUser.getSurname());
                if (incomingUser.getEmail() != null) existingUser.setEmail(incomingUser.getEmail());
                if (incomingUser.getPassword() != null) existingUser.setPassword(incomingUser.getPassword());
                
                userRepository.save(existingUser);
            }
            
            existingPatient.setName(updatedPatient.getName());
            existingPatient.setLastName(updatedPatient.getLastName());
            existingPatient.setDni(updatedPatient.getDni());
            existingPatient.setPhone(updatedPatient.getPhone());
            existingPatient.setEmail(updatedPatient.getEmail());
            existingPatient.setSex(updatedPatient.getSex());
            existingPatient.setSocialSecurityNumber(updatedPatient.getSocialSecurityNumber());
            existingPatient.setTreatmentConsent(updatedPatient.getTreatmentConsent());
            existingPatient.setAnesthesiaConsent(updatedPatient.getAnesthesiaConsent());
            existingPatient.setBilling(updatedPatient.getBilling());
            
            if (updatedPatient.getMedicalRecord() != null) {
                updatedPatient.getMedicalRecord().setPatient(existingPatient);
                existingPatient.setMedicalRecord(updatedPatient.getMedicalRecord());
            }
            
            Patient savedPatient = patientRepository.save(existingPatient);
            
            return ResponseEntity.ok(savedPatient);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Solo el Admin y Auxiliar puede eliminar pacientes
    @DeleteMapping("/{id}")
    @PreAuthorize("@userSecurity.isStaff(authentication)")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        try {
            Optional<Patient> patient = patientRepository.findById(id);
            if (patient.isPresent()) {
                patientRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}