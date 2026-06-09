package com.dynalar.dynalar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "treatment_material")
public class TreatmentMaterial {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "treatment_id")
    @JsonIgnoreProperties("materials")
    private Treatment treatment;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    private Integer quantityRequired;
    
    public TreatmentMaterial() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Integer getQuantityRequired() {
		return quantityRequired;
	}

	public void setQuantityRequired(Integer quantityRequired) {
		this.quantityRequired = quantityRequired;
	}
    
}
