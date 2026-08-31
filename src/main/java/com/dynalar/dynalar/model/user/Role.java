package com.dynalar.dynalar.model.user;

public enum Role {
    ADMIN,
    AUXILIAR,
    DOCTOR,
    PATIENT; 
	
    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}