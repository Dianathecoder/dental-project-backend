package com.dynalar.dynalar.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.user.Dentist;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {
	

	List<Dentist> findByTreatments_Id(Long treatmentId);
	
}