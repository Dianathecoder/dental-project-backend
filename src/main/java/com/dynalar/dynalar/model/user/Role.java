package com.dynalar.dynalar.model.user;


public enum Role {
    SUPERADMIN,  
    OWNER,      
    ADMIN,      
    AUXILIAR,
    DOCTOR,
    PATIENT; 
	
    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}