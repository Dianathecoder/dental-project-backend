package com.dynalar.dynalar.dto.auth;

import com.dynalar.dynalar.model.user.Role;
import java.util.Set;

public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private String surname;
    private Set<Role> roles;
    private String email;

    public AuthResponse(String token, Long userId, String name, String surname, String email, Set<Role> roles) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.roles = roles;
        this.email = email; 
    }
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public Set<Role> getRoles() { return roles; }
    public String getEmail() { return email; }
}
