package com.dynalar.dynalar.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "material")
public class Material {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private Integer availableStock;
	
	private Integer minimumStock;
	
	@OneToMany(mappedBy = "material", fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<TreatmentMaterial> treatments;
	
	public Material() {
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getAvailableStock() {
		return availableStock;
	}
	
	public void setAvailableStock(Integer availableStock) {
		this.availableStock = availableStock;
	}
	
	public Integer getMinimumStock() {
		return minimumStock;
	}
	
	public void setMinimumStock(Integer minimumStock) {
		this.minimumStock = minimumStock;
	}

	public Set<TreatmentMaterial> getTreatments() {
		return treatments;
	}

	public void setTreatments(Set<TreatmentMaterial> treatments) {
		this.treatments = treatments;
	}

}
