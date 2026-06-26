package com.dynalar.dynalar.model.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
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
    private String password;                  // null si es usuario Google

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;            // ← Antes era String "USER"

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


	
		public User() {
	    }

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
	    public Role getRole() { 
	    	return role; 
	    }
	    public void setRole(Role role) { 
	    	this.role = role; 
	    }

	
}
