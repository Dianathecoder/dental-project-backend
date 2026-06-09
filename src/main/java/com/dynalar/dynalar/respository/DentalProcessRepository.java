package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.odontogram.DentalProcess;

@Repository
public interface DentalProcessRepository extends JpaRepository<DentalProcess, Long> {
	
}
