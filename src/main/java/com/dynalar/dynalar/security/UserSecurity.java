package com.dynalar.dynalar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.dynalar.dynalar.respository.PatientRepository;
import com.dynalar.dynalar.respository.UserRepository;


@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    public boolean isSelfOrStaff(Authentication authentication, Long userId) {
        if (isStaff(authentication)) return true;

        String email = authentication.getName();
        return userRepository.findById(userId)
                .map(u -> u.getEmail().equals(email))
                .orElse(false);
    }


    public boolean isSelfOrStaffOrDoctor(Authentication authentication, Long patientId) {
        if (isStaffOrDoctor(authentication)) return true;

        String email = authentication.getName();
        return patientRepository.findById(patientId)
                .map(p -> p.getUser() != null && p.getUser().getEmail().equals(email))
                .orElse(false);
    }

    public boolean isStaff(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN") 
                            || a.getAuthority().equals("ROLE_AUXILIAR") || a.getAuthority().equals("AUXILIAR"));
    }
    
    public boolean isStaffOrDoctor(Authentication authentication) {
        System.out.println("INTENTO DE ACCESO. Usuario: " + authentication.getName());
        System.out.println("Roles en el token: " + authentication.getAuthorities());

        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN")
                        || a.getAuthority().equals("ROLE_AUXILIAR") || a.getAuthority().equals("AUXILIAR")
                        || a.getAuthority().equals("ROLE_DOCTOR") || a.getAuthority().equals("DOCTOR"));
    }
}
