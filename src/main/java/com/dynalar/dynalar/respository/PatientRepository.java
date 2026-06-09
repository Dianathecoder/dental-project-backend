package com.dynalar.dynalar.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.patient.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

	@Query("SELECT p FROM Patient p WHERE " +
			"LOWER(CONCAT(COALESCE(p.name, ''), ' ', COALESCE(p.lastName, ''))) LIKE LOWER(CONCAT('%', :query, '%')) " +
			"OR LOWER(COALESCE(p.dni, '')) LIKE LOWER(CONCAT('%', :query, '%'))")
	Page<Patient> searchPatientsAdvanced(@Param("query") String query, Pageable pageable);
	Page<Patient> findByNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrDniContainingIgnoreCase(String name, String lastName, String dni, Pageable pageable);
	Page<Patient> findByNameStartingWithIgnoreCase(String initial, Pageable pageable);

}
