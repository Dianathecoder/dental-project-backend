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

import com.dynalar.dynalar.dto.TreatmentMaterialRequest;
import com.dynalar.dynalar.model.Material;
import com.dynalar.dynalar.model.Treatment;
import com.dynalar.dynalar.model.TreatmentMaterial;
import com.dynalar.dynalar.respository.MaterialRepository;
import com.dynalar.dynalar.respository.TreatmentMaterialRepository;
import com.dynalar.dynalar.respository.TreatmentRepository;

@RestController
@RequestMapping("/treatment")
public class TreatmentController {

	@Autowired
	private TreatmentRepository treatmentRepository;
	
	@Autowired
	private TreatmentMaterialRepository treatmentMaterialRepository;
	
	@Autowired
	private MaterialRepository materialRepository;


	@GetMapping("/index")
	public ResponseEntity<List<Treatment>> getAllTreatments() {
		try {
			return ResponseEntity.ok(treatmentRepository.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Treatment> getTreatmentById(@PathVariable Long id) {
		try {
			Optional<Treatment> treatment = treatmentRepository.findById(id);
			if (treatment.isPresent()) {
				return ResponseEntity.ok(treatment.get());
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	
	
	@PostMapping()
	public ResponseEntity<Treatment> createTreatment(@RequestBody Treatment treatment) {
		try {
			Treatment newTreatment = treatmentRepository.save(treatment);
			return ResponseEntity.status(HttpStatus.CREATED).body(newTreatment);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}


	@PutMapping("/update")
	public ResponseEntity<Treatment> updateTreatment(@RequestBody Treatment updatedTreatment) {
		try {
			Long id = updatedTreatment.getId();
			if (id == null) {
				return ResponseEntity.badRequest().build();
			}

			Optional<Treatment> existingOpt = treatmentRepository.findById(id);
			if (existingOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Treatment existingTreatment = existingOpt.get();

			existingTreatment.setName(updatedTreatment.getName());
			existingTreatment.setDescription(updatedTreatment.getDescription());
			existingTreatment.setDurationMinutes(updatedTreatment.getDurationMinutes());

			Treatment savedTreatment = treatmentRepository.save(existingTreatment);
			return ResponseEntity.ok(savedTreatment);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTreatment(@PathVariable Long id) {
		try {
			Optional<Treatment> treatment = treatmentRepository.findById(id);
			if (treatment.isPresent()) {
				treatmentRepository.deleteById(id);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	@PostMapping("/{id}/materials")
	public ResponseEntity<Void> addMaterialToTreatment(
	        @PathVariable Long id, 
	        @RequestBody TreatmentMaterialRequest treatmentMaterialRequest) {
	    try {
	        Optional<Treatment> treatmentOpt = treatmentRepository.findById(id);
	        if (treatmentOpt.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        Optional<Material> materialOpt = materialRepository.findById(treatmentMaterialRequest.getMaterialId());
	        if (materialOpt.isEmpty()) {
	            return ResponseEntity.badRequest().build();
	        }

	        Optional<TreatmentMaterial> existingRelation = treatmentMaterialRepository
	                .findByTreatmentIdAndMaterialId(id, treatmentMaterialRequest.getMaterialId());
	        
	        TreatmentMaterial treatmentMaterial;
	        
	        if (existingRelation.isPresent()) {
	            treatmentMaterial = existingRelation.get();
	            treatmentMaterial.setQuantityRequired(treatmentMaterialRequest.getQuantityRequired());
	        } else {
	            treatmentMaterial = new TreatmentMaterial();
	            treatmentMaterial.setTreatment(treatmentOpt.get());
	            treatmentMaterial.setMaterial(materialOpt.get());
	            treatmentMaterial.setQuantityRequired(treatmentMaterialRequest.getQuantityRequired());
	        }

	        treatmentMaterialRepository.save(treatmentMaterial);
	        return ResponseEntity.ok().build();
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	
	@PutMapping("/{id}/materials/{materialId}")
	public ResponseEntity<Void> updateMaterialInTreatment(
	        @PathVariable Long id, 
	        @PathVariable Long materialId, 
	        @RequestBody TreatmentMaterialRequest treatmentMaterialRequest) {
	    try {
	        Optional<TreatmentMaterial> relationOpt = treatmentMaterialRepository
	                .findByTreatmentIdAndMaterialId(id, materialId);

	        if (relationOpt.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        TreatmentMaterial treatmentMaterial = relationOpt.get();
	        treatmentMaterial.setQuantityRequired(treatmentMaterialRequest.getQuantityRequired());
	        treatmentMaterialRepository.save(treatmentMaterial);
	        
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	
	@DeleteMapping("/{id}/materials/{materialId}")
	public ResponseEntity<Void> removeMaterialFromTreatment(
	        @PathVariable Long id, 
	        @PathVariable Long materialId) {
	    try {
	        Optional<TreatmentMaterial> relationOpt = treatmentMaterialRepository
	                .findByTreatmentIdAndMaterialId(id, materialId);

	        if (relationOpt.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        }

	        treatmentMaterialRepository.delete(relationOpt.get());
	        
	        return ResponseEntity.noContent().build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

}