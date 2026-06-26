package com.dynalar.dynalar.controller;

import java.util.List;
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

import com.dynalar.dynalar.model.user.Dentist;
import com.dynalar.dynalar.model.user.Role;
import com.dynalar.dynalar.respository.DentistRepository;

@RestController
@RequestMapping("/dentist")
public class DentistController {

	@Autowired
	private DentistRepository dentistRepository;
	

	@PostMapping()
	public ResponseEntity<Dentist> createDentist(@RequestBody Dentist dentist) {
		try {
			dentist.setRole(Role.DENTIST);
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


	@PutMapping("/update")
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


			if (updatedDentist.getName() != null) existingDentist.setName(updatedDentist.getName());
			if (updatedDentist.getSurname() != null) existingDentist.setSurname(updatedDentist.getSurname());
			if (updatedDentist.getEmail() != null) existingDentist.setEmail(updatedDentist.getEmail());
			if (updatedDentist.getPassword() != null) existingDentist.setPassword(updatedDentist.getPassword());


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


			if (updatedDentist.getTreatments() != null) {
				existingDentist.setTreatments(updatedDentist.getTreatments());
			}

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