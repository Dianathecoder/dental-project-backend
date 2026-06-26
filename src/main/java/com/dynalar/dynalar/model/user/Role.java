package com.dynalar.dynalar.model.user;


public enum Role {

    USER,      
    ADMIN,     
    DENTIST;    


    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
