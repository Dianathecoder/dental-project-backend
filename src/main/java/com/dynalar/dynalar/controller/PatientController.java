package com.dynalar.dynalar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

import com.dynalar.dynalar.model.odontogram.Odontogram;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.PatientRepository;
import com.dynalar.dynalar.respository.UserRepository;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Solo Admin, Auxiliar o Doctor pueden ver la lista general
    @GetMapping("/index")
    @PreAuthorize("@userSecurity.isStaffOrDoctor(authentication)")
    public ResponseEntity<Page<Patient>> getAllPatients(
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
            return ResponseEntity.status(404).build();
        }
    }
    
    // Solo el staff administrativo crea pacientes manualmente
    @PostMapping
    @PreAuthorize("@userSecurity.isStaff(authentication)")
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        try {
            if (patient.getUser() != null && patient.getUser().getId() != null) {
                User existingUser = userRepository.findById(patient.getUser().getId())
                                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
                existingUser.getRoles().add(Role.PATIENT);
                userRepository.save(existingUser);
                
                patient.setUser(existingUser);
            }

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
            
            Patient savedPatient = patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin, Auxiliar, Doctor o el Paciente dueño pueden ver su propio perfil
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
    
    // Solo Admin, Auxiliar o Doctor pueden buscar
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
    
    // El dueño, el admin, auxiliar o doctor pueden actualizar
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

    // Solo el staff (Admin/Auxiliar) puede eliminar pacientes
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