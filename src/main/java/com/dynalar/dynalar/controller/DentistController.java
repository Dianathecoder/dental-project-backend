package com.dynalar.dynalar.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.dynalar.dynalar.dto.DentistAvailabilityDTO;
import com.dynalar.dynalar.model.Treatment;
import com.dynalar.dynalar.model.user.Dentist;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.DentistRepository;
import com.dynalar.dynalar.respository.TreatmentRepository;
import com.dynalar.dynalar.respository.UserRepository;

@RestController
@RequestMapping("/dentist")
public class DentistController {

    @Autowired
    private DentistRepository dentistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<Dentist> createDentist(@RequestBody Dentist dentist) {
        try {
            if (dentist.getUser() == null || dentist.getUser().getId() == null) {
                return ResponseEntity.badRequest().build();
            }

            User existingUser = userRepository.findById(dentist.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            existingUser.getRoles().add(Role.DOCTOR);
            userRepository.save(existingUser);

            dentist.setUser(existingUser);
            Dentist newDentist = dentistRepository.save(dentist);

            return ResponseEntity.status(HttpStatus.CREATED).body(newDentist);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<DentistAvailabilityDTO> getAvailabilityByUserId(@PathVariable Long userId) {
        Dentist dentist = dentistRepository.findByUserId(userId).orElse(null);

        DentistAvailabilityDTO dto = new DentistAvailabilityDTO();
        if (dentist != null) {
            // LUNES
            dto.setMondayMorningActive(Boolean.TRUE.equals(dentist.getMondayMorningActive()));
            dto.setMondayMorningStart(dentist.getMondayMorningStart() != null ? dentist.getMondayMorningStart() : "09:00");
            dto.setMondayMorningEnd(dentist.getMondayMorningEnd() != null ? dentist.getMondayMorningEnd() : "14:00");
            dto.setMondayAfternoonActive(Boolean.TRUE.equals(dentist.getMondayAfternoonActive()));
            dto.setMondayAfternoonStart(dentist.getMondayAfternoonStart() != null ? dentist.getMondayAfternoonStart() : "15:00");
            dto.setMondayAfternoonEnd(dentist.getMondayAfternoonEnd() != null ? dentist.getMondayAfternoonEnd() : "20:00");
            dto.setMondayEveningActive(Boolean.TRUE.equals(dentist.getMondayEveningActive()));
            dto.setMondayEveningStart(dentist.getMondayEveningStart() != null ? dentist.getMondayEveningStart() : "20:00");
            dto.setMondayEveningEnd(dentist.getMondayEveningEnd() != null ? dentist.getMondayEveningEnd() : "22:00");

            // MARTES
            dto.setTuesdayMorningActive(Boolean.TRUE.equals(dentist.getTuesdayMorningActive()));
            dto.setTuesdayMorningStart(dentist.getTuesdayMorningStart() != null ? dentist.getTuesdayMorningStart() : "09:00");
            dto.setTuesdayMorningEnd(dentist.getTuesdayMorningEnd() != null ? dentist.getTuesdayMorningEnd() : "14:00");
            dto.setTuesdayAfternoonActive(Boolean.TRUE.equals(dentist.getTuesdayAfternoonActive()));
            dto.setTuesdayAfternoonStart(dentist.getTuesdayAfternoonStart() != null ? dentist.getTuesdayAfternoonStart() : "15:00");
            dto.setTuesdayAfternoonEnd(dentist.getTuesdayAfternoonEnd() != null ? dentist.getTuesdayAfternoonEnd() : "20:00");
            dto.setTuesdayEveningActive(Boolean.TRUE.equals(dentist.getTuesdayEveningActive()));
            dto.setTuesdayEveningStart(dentist.getTuesdayEveningStart() != null ? dentist.getTuesdayEveningStart() : "20:00");
            dto.setTuesdayEveningEnd(dentist.getTuesdayEveningEnd() != null ? dentist.getTuesdayEveningEnd() : "22:00");

            // MIÉRCOLES
            dto.setWednesdayMorningActive(Boolean.TRUE.equals(dentist.getWednesdayMorningActive()));
            dto.setWednesdayMorningStart(dentist.getWednesdayMorningStart() != null ? dentist.getWednesdayMorningStart() : "09:00");
            dto.setWednesdayMorningEnd(dentist.getWednesdayMorningEnd() != null ? dentist.getWednesdayMorningEnd() : "14:00");
            dto.setWednesdayAfternoonActive(Boolean.TRUE.equals(dentist.getWednesdayAfternoonActive()));
            dto.setWednesdayAfternoonStart(dentist.getWednesdayAfternoonStart() != null ? dentist.getWednesdayAfternoonStart() : "15:00");
            dto.setWednesdayAfternoonEnd(dentist.getWednesdayAfternoonEnd() != null ? dentist.getWednesdayAfternoonEnd() : "20:00");
            dto.setWednesdayEveningActive(Boolean.TRUE.equals(dentist.getWednesdayEveningActive()));
            dto.setWednesdayEveningStart(dentist.getWednesdayEveningStart() != null ? dentist.getWednesdayEveningStart() : "20:00");
            dto.setWednesdayEveningEnd(dentist.getWednesdayEveningEnd() != null ? dentist.getWednesdayEveningEnd() : "22:00");

            // JUEVES
            dto.setThursdayMorningActive(Boolean.TRUE.equals(dentist.getThursdayMorningActive()));
            dto.setThursdayMorningStart(dentist.getThursdayMorningStart() != null ? dentist.getThursdayMorningStart() : "09:00");
            dto.setThursdayMorningEnd(dentist.getThursdayMorningEnd() != null ? dentist.getThursdayMorningEnd() : "14:00");
            dto.setThursdayAfternoonActive(Boolean.TRUE.equals(dentist.getThursdayAfternoonActive()));
            dto.setThursdayAfternoonStart(dentist.getThursdayAfternoonStart() != null ? dentist.getThursdayAfternoonStart() : "15:00");
            dto.setThursdayAfternoonEnd(dentist.getThursdayAfternoonEnd() != null ? dentist.getThursdayAfternoonEnd() : "20:00");
            dto.setThursdayEveningActive(Boolean.TRUE.equals(dentist.getThursdayEveningActive()));
            dto.setThursdayEveningStart(dentist.getThursdayEveningStart() != null ? dentist.getThursdayEveningStart() : "20:00");
            dto.setThursdayEveningEnd(dentist.getThursdayEveningEnd() != null ? dentist.getThursdayEveningEnd() : "22:00");

            // VIERNES
            dto.setFridayMorningActive(Boolean.TRUE.equals(dentist.getFridayMorningActive()));
            dto.setFridayMorningStart(dentist.getFridayMorningStart() != null ? dentist.getFridayMorningStart() : "09:00");
            dto.setFridayMorningEnd(dentist.getFridayMorningEnd() != null ? dentist.getFridayMorningEnd() : "14:00");
            dto.setFridayAfternoonActive(Boolean.TRUE.equals(dentist.getFridayAfternoonActive()));
            dto.setFridayAfternoonStart(dentist.getFridayAfternoonStart() != null ? dentist.getFridayAfternoonStart() : "15:00");
            dto.setFridayAfternoonEnd(dentist.getFridayAfternoonEnd() != null ? dentist.getFridayAfternoonEnd() : "20:00");
            dto.setFridayEveningActive(Boolean.TRUE.equals(dentist.getFridayEveningActive()));
            dto.setFridayEveningStart(dentist.getFridayEveningStart() != null ? dentist.getFridayEveningStart() : "20:00");
            dto.setFridayEveningEnd(dentist.getFridayEveningEnd() != null ? dentist.getFridayEveningEnd() : "22:00");

            if (dentist.getTreatments() != null) {
                dto.setTreatmentIds(dentist.getTreatments().stream().map(Treatment::getId).collect(Collectors.toList()));
            }
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/user/{userId}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateAvailabilityByUserId(@PathVariable Long userId, @RequestBody DentistAvailabilityDTO dto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Dentist dentist = dentistRepository.findByUserId(userId).orElseGet(() -> {
                Dentist d = new Dentist();
                d.setUser(user);
                user.getRoles().add(Role.DOCTOR);
                userRepository.save(user);
                return d;
            });

            // LUNES
            dentist.setMondayMorningActive(dto.getMondayMorningActive());
            dentist.setMondayMorningStart(dto.getMondayMorningStart());
            dentist.setMondayMorningEnd(dto.getMondayMorningEnd());
            dentist.setMondayAfternoonActive(dto.getMondayAfternoonActive());
            dentist.setMondayAfternoonStart(dto.getMondayAfternoonStart());
            dentist.setMondayAfternoonEnd(dto.getMondayAfternoonEnd());
            dentist.setMondayEveningActive(dto.getMondayEveningActive());
            dentist.setMondayEveningStart(dto.getMondayEveningStart());
            dentist.setMondayEveningEnd(dto.getMondayEveningEnd());

            // MARTES
            dentist.setTuesdayMorningActive(dto.getTuesdayMorningActive());
            dentist.setTuesdayMorningStart(dto.getTuesdayMorningStart());
            dentist.setTuesdayMorningEnd(dto.getTuesdayMorningEnd());
            dentist.setTuesdayAfternoonActive(dto.getTuesdayAfternoonActive());
            dentist.setTuesdayAfternoonStart(dto.getTuesdayAfternoonStart());
            dentist.setTuesdayAfternoonEnd(dto.getTuesdayAfternoonEnd());
            dentist.setTuesdayEveningActive(dto.getTuesdayEveningActive());
            dentist.setTuesdayEveningStart(dto.getTuesdayEveningStart());
            dentist.setTuesdayEveningEnd(dto.getTuesdayEveningEnd());

            // MIÉRCOLES
            dentist.setWednesdayMorningActive(dto.getWednesdayMorningActive());
            dentist.setWednesdayMorningStart(dto.getWednesdayMorningStart());
            dentist.setWednesdayMorningEnd(dto.getWednesdayMorningEnd());
            dentist.setWednesdayAfternoonActive(dto.getWednesdayAfternoonActive());
            dentist.setWednesdayAfternoonStart(dto.getWednesdayAfternoonStart());
            dentist.setWednesdayAfternoonEnd(dto.getWednesdayAfternoonEnd());
            dentist.setWednesdayEveningActive(dto.getWednesdayEveningActive());
            dentist.setWednesdayEveningStart(dto.getWednesdayEveningStart());
            dentist.setWednesdayEveningEnd(dto.getWednesdayEveningEnd());

            // JUEVES
            dentist.setThursdayMorningActive(dto.getThursdayMorningActive());
            dentist.setThursdayMorningStart(dto.getThursdayMorningStart());
            dentist.setThursdayMorningEnd(dto.getThursdayMorningEnd());
            dentist.setThursdayAfternoonActive(dto.getThursdayAfternoonActive());
            dentist.setThursdayAfternoonStart(dto.getThursdayAfternoonStart());
            dentist.setThursdayAfternoonEnd(dto.getThursdayAfternoonEnd());
            dentist.setThursdayEveningActive(dto.getThursdayEveningActive());
            dentist.setThursdayEveningStart(dto.getThursdayEveningStart());
            dentist.setThursdayEveningEnd(dto.getThursdayEveningEnd());

            // VIERNES
            dentist.setFridayMorningActive(dto.getFridayMorningActive());
            dentist.setFridayMorningStart(dto.getFridayMorningStart());
            dentist.setFridayMorningEnd(dto.getFridayMorningEnd());
            dentist.setFridayAfternoonActive(dto.getFridayAfternoonActive());
            dentist.setFridayAfternoonStart(dto.getFridayAfternoonStart());
            dentist.setFridayAfternoonEnd(dto.getFridayAfternoonEnd());
            dentist.setFridayEveningActive(dto.getFridayEveningActive());
            dentist.setFridayEveningStart(dto.getFridayEveningStart());
            dentist.setFridayEveningEnd(dto.getFridayEveningEnd());

            if (dto.getTreatmentIds() != null) {
                List<Treatment> treatments = treatmentRepository.findAllById(dto.getTreatmentIds());
                dentist.setTreatments(new HashSet<>(treatments));
            } else {
                dentist.setTreatments(new HashSet<>());
            }

            dentistRepository.save(dentist);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/treatment/{treatmentId}")
    public ResponseEntity<List<User>> getDentistsByTreatment(@PathVariable Long treatmentId) {
        try {
            List<Dentist> dentists = dentistRepository.findByTreatments_Id(treatmentId);
            
        
            List<User> users = dentists.stream()
                                       .map(Dentist::getUser)
                                       .collect(Collectors.toList());
                                       
            return ResponseEntity.ok(users);
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
            return dentist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN')")
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