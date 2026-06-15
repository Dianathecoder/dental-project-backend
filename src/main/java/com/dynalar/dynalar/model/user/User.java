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

@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String surname;
	
	@Column(unique = true, nullable = false)
	private String email;
	 @Column(name = "password_hash")
	private String password;
	 
	
	private String avatarUrl;
	
	@Enumerated(EnumType.STRING)
    private Provider provider = Provider.LOCAL;
	
	 private String googleId;
	    private boolean emailVerified = false;
	    private String preferredLanguage = "es";

	@CreationTimestamp
	private LocalDateTime createdAt;

	private String role;
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
	    public String getRole() { 
	    	return role; 
	    }
	    public void setRole(String role) { 
	    	this.role = role; 
	    }

	
}
