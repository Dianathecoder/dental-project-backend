package com.dynalar.dynalar.respository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.Material;
import com.dynalar.dynalar.model.TreatmentMaterial;

@Repository
public interface TreatmentMaterialRepository extends JpaRepository<TreatmentMaterial, Long> {
	
	Optional<TreatmentMaterial> findByTreatmentIdAndMaterialId(Long treatmentId, Long materialId);
	public List<Material> findMaterialsByTreatmentId(Long treatmentId);
	
}
