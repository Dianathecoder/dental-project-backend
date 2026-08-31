package com.dynalar.dynalar.controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynalar.dynalar.model.user.Dentist;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.DentistRepository;
import com.dynalar.dynalar.respository.UserRepository;

@RestController
@RequestMapping("/dentist")
public class DentistController {

    @Autowired
    private DentistRepository dentistRepository;
    
  
    @Autowired
    private UserRepository userRepository;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<Dentist> createDentist(@RequestBody Dentist dentist) {
        try {
            // 1. Verificamos que se haya enviado el id del usuario base
            if (dentist.getUser() == null || dentist.getUser().getId() == null) {
                return ResponseEntity.badRequest().build(); 
            }
            
            // 2. Buscamos al usuario base
            User existingUser = userRepository.findById(dentist.getUser().getId())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // 3. Le añadimos el rol de DOCTOR a su colección de roles y guardamos
            existingUser.getRoles().add(Role.DOCTOR);
            userRepository.save(existingUser);

            // 4. Vinculamos el usuario al perfil de dentista y guardamos
            dentist.setUser(existingUser);
            Dentist newDentist = dentistRepository.save(dentist);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newDentist);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/treatment/{treatmentId}")
    public ResponseEntity<List<Dentist>> getDentistsByTreatment(@PathVariable Long treatmentId) {
        try {
            List<Dentist> dentists = dentistRepository.findByTreatments_Id(treatmentId);
            if (dentists.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(dentists);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/index")
    public ResponseEntity<List<Dentist>> getAllDentists() {
        try {
            return ResponseEntity.ok(dentistRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dentist> getDentistById(@PathVariable Long id) {
        try {
            Optional<Dentist> dentist = dentistRepository.findById(id);
            if (dentist.isPresent()) {
                return ResponseEntity.ok(dentist.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // El Doctor NO tiene permiso
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<Dentist> updateDentist(@RequestBody Dentist updatedDentist) {
        try {
            Long id = updatedDentist.getId();
            if (id == null) {
                return ResponseEntity.badRequest().build();
            }

            Optional<Dentist> existingOpt = dentistRepository.findById(id);
            if (existingOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Dentist existingDentist = existingOpt.get();

            //Actualizar los datos del usuario 
            if (updatedDentist.getUser() != null) {
                User existingUser = existingDentist.getUser();
                User incomingUser = updatedDentist.getUser();

                if (existingUser != null) {
                    if (incomingUser.getName() != null) existingUser.setName(incomingUser.getName());
                    if (incomingUser.getSurname() != null) existingUser.setSurname(incomingUser.getSurname());
                    if (incomingUser.getEmail() != null) existingUser.setEmail(incomingUser.getEmail());
                    if (incomingUser.getPassword() != null) existingUser.setPassword(incomingUser.getPassword());
                    
                    userRepository.save(existingUser); // Se guarda el usuario base actualizado
                }
            }

            // Actualizar los horarios.Solo Admin/Auxiliar
            existingDentist.setMondayMorning(updatedDentist.getMondayMorning());
            existingDentist.setMondayAfternoon(updatedDentist.getMondayAfternoon());
            existingDentist.setTuesdayMorning(updatedDentist.getTuesdayMorning());
            existingDentist.setTuesdayAfternoon(updatedDentist.getTuesdayAfternoon());
            existingDentist.setWednesdayMorning(updatedDentist.getWednesdayMorning());
            existingDentist.setWednesdayAfternoon(updatedDentist.getWednesdayAfternoon());
            existingDentist.setThursdayMorning(updatedDentist.getThursdayMorning());
            existingDentist.setThursdayAfternoon(updatedDentist.getThursdayAfternoon());
            existingDentist.setFridayMorning(updatedDentist.getFridayMorning());
            existingDentist.setFridayAfternoon(updatedDentist.getFridayAfternoon());

            //Actualizar tratamientos
            if (updatedDentist.getTreatments() != null) {
                existingDentist.setTreatments(updatedDentist.getTreatments());
            }

         //Guardar tratamientos
            Dentist savedDentist = dentistRepository.save(existingDentist);
            return ResponseEntity.ok(savedDentist);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDentist(@PathVariable Long id) {
        try {
            Optional<Dentist> existingOpt = dentistRepository.findById(id);
            if (existingOpt.isPresent()) {
                dentistRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}