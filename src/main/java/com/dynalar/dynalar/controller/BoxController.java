package com.dynalar.dynalar.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dynalar.dynalar.model.Box;
import com.dynalar.dynalar.respository.AppointmentRepository;
import com.dynalar.dynalar.respository.BoxRepository;

@RestController
@RequestMapping("/box")
public class BoxController {

	@Autowired
	private BoxRepository boxRepository;
	
	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@PostMapping()
	public ResponseEntity<Box> createBox(@RequestBody Box box) {
		try {
			if (boxRepository.existsById(box.getNumber())) {
	            return ResponseEntity.badRequest().build();
	        }
	        return ResponseEntity.ok(boxRepository.save(box));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/all")
	public ResponseEntity<Iterable<Box>> getAllBoxes() {
		try {
			return ResponseEntity.ok(boxRepository.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/{number}")
	public ResponseEntity<Box> getBoxByNumber(@PathVariable Integer number) {
		try {
			return boxRepository.findById(number)
					.map(ResponseEntity::ok)
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@DeleteMapping("/{number}")
	public ResponseEntity<Void> deleteBox(@PathVariable Integer number) {
		try {
			if (!boxRepository.existsById(number)) {
				return ResponseEntity.notFound().build();
			}
			
			LocalDateTime now = LocalDateTime.now();
			if (appointmentRepository.existsByBox_NumberAndStartTimeAfter(number, now)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			
			boxRepository.deleteById(number);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
