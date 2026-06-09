package com.dynalar.dynalar.dto;

public class TreatmentMaterialRequest {

	private Long materialId;
    private Integer quantityRequired;
    
    public TreatmentMaterialRequest() {
    	
    }
    
	public Long getMaterialId() {
		return materialId;
	}
	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}
	public Integer getQuantityRequired() {
		return quantityRequired;
	}
	public void setQuantityRequired(Integer quantityRequired) {
		this.quantityRequired = quantityRequired;
	}

    
}
