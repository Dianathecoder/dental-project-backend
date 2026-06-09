package com.dynalar.dynalar.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dynalar.dynalar.model.odontogram.Odontogram;

public interface OdontogramRepository extends JpaRepository<Odontogram, Long> {

	public Optional<Odontogram> findByPatient_Id(Long patientId);
}
