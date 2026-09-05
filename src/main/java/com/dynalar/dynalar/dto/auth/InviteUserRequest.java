package com.dynalar.dynalar.dto.auth;

import lombok.Getter;
import lombok.Setter;

public class InviteUserRequest {
    private String name;
    private String surname;
    private String email;
    private String role;
    private String dni;
    private String phone;
    private String sex;

    // --- GETTERS ---
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getDni() { return dni; }
    public String getPhone() { return phone; }
    public String getSex() { return sex; }

    // --- SETTERS ---
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setDni(String dni) { this.dni = dni; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSex(String sex) { this.sex = sex; }
}