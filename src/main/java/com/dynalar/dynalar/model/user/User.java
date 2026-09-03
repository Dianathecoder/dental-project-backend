package com.dynalar.dynalar.model.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.dynalar.dynalar.model.patient.Patient;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String surname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "password_hash")
    private String password;                
    
    private String avatarUrl;

    @Column(name = "failed_attempt")
    private Integer failedAttempt = 0;

    // Cambiado a Boolean para permitir valores NULL de la base de datos sin lanzar excepciones
    @Column(name = "account_non_locked")
    private Boolean accountNonLocked = true;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;
    
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_name")
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider = Provider.LOCAL;

    private String googleId;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private String preferredLanguage = "es";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum Provider { LOCAL, GOOGLE }

    @OneToOne(mappedBy = "user")
    @JsonIgnore 
    private Patient patient;
    
    public Long getId() { 
        return id; 
    }
    public String getName() {
        return name; 
    }
    public void setName(String name) { 
        this.name = name;
    }
    public String getSurname() { 
        return surname; 
    }
    public void setSurname(String surname) { 
        this.surname = surname; 
    }
    public String getEmail() {
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }
    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    public String getAvatarUrl() { 
        return avatarUrl; 
    }
    public void setAvatarUrl(String avatarUrl) { 
        this.avatarUrl = avatarUrl;
    }
    public Provider getProvider() { 
        return provider; 
    }
    public void setProvider(Provider provider) { 
        this.provider = provider; 
    }
    public String getGoogleId() { 
        return googleId; 
    }
    public void setGoogleId(String googleId) { 
        this.googleId = googleId; 
    }
    public boolean isEmailVerified() { 
        return emailVerified; 
    }
    public void setEmailVerified(boolean emailVerified) { 
        this.emailVerified = emailVerified;
    }
    public String getPreferredLanguage() {
        return preferredLanguage; 
    }
    public void setPreferredLanguage(String preferredLanguage) { 
        this.preferredLanguage = preferredLanguage;
    }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public int getFailedAttempt() { 
        return failedAttempt == null ? 0 : failedAttempt; 
    }

    public void setFailedAttempt(Integer failedAttempt) { 
        this.failedAttempt = failedAttempt; 
    }

    public boolean isAccountNonLocked() { 
        return accountNonLocked == null || accountNonLocked; 
    }
    public void setAccountNonLocked(Boolean accountNonLocked) { 
        this.accountNonLocked = accountNonLocked; 
    }

    public LocalDateTime getLockTime() { 
        return lockTime; 
    }
    public void setLockTime(LocalDateTime lockTime) { 
        this.lockTime = lockTime; 
    }
}