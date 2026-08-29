package com.dynalar.dynalar.dto.auth;

import com.dynalar.dynalar.model.user.Role;

public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private String surname;
    private Role role;
    private String email;

    public AuthResponse(String token, Long userId, String name, String surname, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.email = email; 
    }
    
    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public Role getRole() { return role; }
    public String getEmail() { return email; }
}
