package com.dynalar.dynalar.model.user;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private boolean used = false;

    public PasswordResetToken() {}

    public Long getId() { 
    	return id; 
    	}
    public User getUser() { 
    	return user; }
    public void setUser(User user) { 
    	this.user = user; 
    	}
    public String getToken() { 
    	return token; 
    	}
    public void setToken(String token) { 
    	this.token = token; 
    	}
    public LocalDateTime getExpiresAt() {
    	return expiresAt;
    	}
    public void setExpiresAt(LocalDateTime expiresAt) { 
    	this.expiresAt = expiresAt; 
    	}
    public boolean isUsed() { 
    	return used; 
    	}
    public void setUsed(boolean used) { 
    	this.used = used;
    	}
}