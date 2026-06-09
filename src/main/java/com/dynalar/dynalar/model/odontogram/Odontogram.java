package com.dynalar.dynalar.model.odontogram;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dynalar.dynalar.model.patient.Patient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "odontogram")
public class Odontogram {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalDateTime creationDate;
	
	private LocalDateTime modificationDate;
	
	@OneToOne
	@JoinColumn(name = "patient_id")
	@JsonIgnore
	private Patient patient;
	
	@OneToMany(mappedBy = "odontogram", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OdontogramEntry> odontogramEntries = new ArrayList<>();
    
    public Odontogram() {
    	this.creationDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	
	public LocalDateTime getModificationDate() {
		return modificationDate;
	}
	
	public void setModificationDate(LocalDateTime modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public List<OdontogramEntry> getOdontogramEntries() {
		return odontogramEntries;
	}
	
	public void setOdontogramEntries(List<OdontogramEntry> odontogramEntries) {
		this.odontogramEntries = odontogramEntries;
	}
}
