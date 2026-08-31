package com.dynalar.dynalar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.dynalar.dynalar.respository.ChatConversationRepository;
import com.dynalar.dynalar.respository.PatientRepository;


//Se encargará de comprobar si el usuario que hace la petición tiene derecho a ver esa conversación
@Component("chatSecurity")
public class ChatSecurity {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ChatConversationRepository chatRepository;

    
    public boolean canAccessPatientChats(Authentication authentication, Long patientId) {
        if (isStaff(authentication)) return true;

        String email = authentication.getName(); 
        return patientRepository.findById(patientId)
                .map(p -> p.getUser() != null && p.getUser().getEmail().equals(email))
                .orElse(false);
    }

    // Verifica si el usuario del token pertenece a la conversación o es staff
    public boolean canAccessConversation(Authentication authentication, Long conversationId) {
        if (isStaff(authentication)) return true;

        String email = authentication.getName();
        return chatRepository.findById(conversationId)
                .map(c -> c.getPatient().getUser() != null && c.getPatient().getUser().getEmail().equals(email))
                .orElse(false);
    }

    private boolean isStaff(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_AUXILIAR"));
    }
}