package com.dynalar.dynalar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynalar.dynalar.model.user.User; 
import com.dynalar.dynalar.respository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    // Solo el staff (ADMIN o AUXILIAR) puede ver la lista de todos los usuarios del sistema
    @GetMapping("/all")
    @PreAuthorize("@userSecurity.isStaff(authentication)")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
    
    // Un usuario puede ver su propia cuenta, o el staff puede ver la de cualquiera
    @GetMapping("/{id}")
    @PreAuthorize("@userSecurity.isSelfOrStaff(authentication, #id)")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setPassword(null); 
            return ResponseEntity.ok(foundUser);
        }
        
        return ResponseEntity.notFound().build();
    }
}