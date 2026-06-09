package com.dynalar.dynalar.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynalar.dynalar.model.odontogram.Odontogram;
import com.dynalar.dynalar.model.odontogram.OdontogramEntry;
import com.dynalar.dynalar.model.patient.Patient;
import com.dynalar.dynalar.respository.OdontogramRepository;
import com.dynalar.dynalar.respository.PatientRepository;

@RestController
@RequestMapping("/odontogram")
public class OdontogramController {

	@Autowired
	private OdontogramRepository odontogramRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Odontogram> getOdontogramById(@PathVariable Long id) {
		try {
			Optional<Odontogram> odontogramOpt = odontogramRepository.findById(id);
			return ResponseEntity.ok(odontogramOpt.orElse(null));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(404).build();
		}
	}
	
	
	// El odontograma se crea cuando se crea el paciente 
	/*
	@PostMapping("/patient/{patientId}")
	public ResponseEntity<Odontogram> createOdontogram(@PathVariable Long patientId, @RequestBody Odontogram odontogram) {
		try {
			Optional<Patient> patientOpt = patientRepository.findById(patientId);
			if (patientOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
			odontogram.setPatientId(patientOpt.get());
            odontogram.setCreationDate(LocalDateTime.now());
            Odontogram savedOdontogram = odontogramRepository.save(odontogram);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOdontogram);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	*/
	
	
	@PutMapping("/{id}")
	public ResponseEntity<Void> updateOdontogram(@PathVariable Long id, @RequestBody Odontogram updatedOdontogram) {
		try {
			
			Optional<Odontogram> existingOpt = odontogramRepository.findById(id);
			
			if (existingOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			
			Odontogram existingOdontogram = existingOpt.get();
			
			existingOdontogram.setModificationDate(LocalDateTime.now());
			existingOdontogram.getOdontogramEntries().clear();

			if (updatedOdontogram.getOdontogramEntries() != null) {
			    for (OdontogramEntry entry : updatedOdontogram.getOdontogramEntries()) {
			        entry.setOdontogram(existingOdontogram);
			        existingOdontogram.getOdontogramEntries().add(entry);
			    }
			}
            odontogramRepository.save(existingOdontogram);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOdontogram(@PathVariable Long id) {
		try {
			Optional<Odontogram> existingOpt = odontogramRepository.findById(id);
			
			if (existingOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			
			Odontogram existingOdontogram = existingOpt.get();
			
			odontogramRepository.delete(existingOdontogram);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
