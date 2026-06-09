package com.dynalar.dynalar.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dynalar.dynalar.model.patient.Document;

public interface DocumentRepository extends JpaRepository<Document, Long>{
	List<Document> findByPatientId(Long patientId);
}
