
package com.dynalar.dynalar.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.Optional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.dynalar.dynalar.model.odontogram.Odontogram;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.respository.PatientRepository;

@RestController
@RequestMapping("/patient")
public class PatientController {

	@Autowired
	private PatientRepository patientRepository;
	
	@GetMapping("/index")
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
	
	@PostMapping
	public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
	    try {
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

	
	@GetMapping("/{id}")
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
	
	@GetMapping("/search")
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
	        
	        existingPatient.setName(updatedPatient.getName());
	        existingPatient.setLastName(updatedPatient.getLastName());
	        existingPatient.setDni(updatedPatient.getDni());
	        existingPatient.setPhone(updatedPatient.getPhone());
	        existingPatient.setEmail(updatedPatient.getEmail());
	        existingPatient.setSex(updatedPatient.getSex());
	        
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

	@DeleteMapping("/{id}")
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
