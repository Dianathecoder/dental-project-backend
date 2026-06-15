package com.dynalar.dynalar.dto.auth;

public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private String surname;
    private String role;

    public AuthResponse(String token, Long userId, String name, String surname, String role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.role = role;
    }

    public String getToken() { return token; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getRole() { return role; }
}