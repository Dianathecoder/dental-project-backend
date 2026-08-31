package com.dynalar.dynalar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.dynalar.dynalar.ChatConversation.ChatConversation;
import com.dynalar.dynalar.ChatConversation.ChatMessage;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.ChatConversationRepository;
import com.dynalar.dynalar.respository.ChatMessageRepository;
import com.dynalar.dynalar.respository.UserRepository;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatConversationRepository conversationRepository;
    
    @Autowired
    private ChatMessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;

    // --- CONVERSACIONES ---

    // Solo el staff o el propio paciente pueden crear un chat para este paciente
    @PostMapping("/conversation")
    @PreAuthorize("@chatSecurity.canAccessPatientChats(authentication, #conversation.patient.id)")
    public ResponseEntity<ChatConversation> createConversation(@RequestBody ChatConversation conversation) {
        try {
            if (conversation.getPatient() == null || conversation.getPatient().getId() == null) {
                return ResponseEntity.badRequest().build();
            }
            ChatConversation savedConv = conversationRepository.save(conversation);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedConv);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Solo ADMIN y AUXILIAR pueden ver la bandeja de entrada global
    @GetMapping("/conversations/open")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<List<ChatConversation>> getOpenConversations() {
        return ResponseEntity.ok(conversationRepository.findByStatus("OPEN"));
    }

    // Un paciente solo puede ver sus propios chats (o el staff puede verlos)
    @GetMapping("/conversations/patient/{patientId}")
    @PreAuthorize("@chatSecurity.canAccessPatientChats(authentication, #patientId)")
    public ResponseEntity<List<ChatConversation>> getPatientConversations(@PathVariable Long patientId) {
        return ResponseEntity.ok(conversationRepository.findByPatientId(patientId));
    }

    // --- MENSAJES ---

    // El historial solo lo ve el dueño del chat o el staff
    @GetMapping("/conversation/{conversationId}/messages")
    @PreAuthorize("@chatSecurity.canAccessConversation(authentication, #conversationId)")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long conversationId) {
        return ResponseEntity.ok(messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId));
    }

    // Enviar mensaje (Se saca el usuario real del token, no del RequestParam)
    @PostMapping("/conversation/{conversationId}/message")
    @PreAuthorize("@chatSecurity.canAccessConversation(authentication, #conversationId)")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable Long conversationId, 
            @RequestBody String messageText,
            Authentication authentication) { // <-- Se inyecta la autenticación actual
        try {
            Optional<ChatConversation> convOpt = conversationRepository.findById(conversationId);
            
            // Sacamos el usuario de la BD basándonos en el token JWT
            Optional<User> senderOpt = userRepository.findByEmail(authentication.getName());

            if (convOpt.isEmpty() || senderOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            ChatMessage message = new ChatMessage();
            message.setConversation(convOpt.get());
            message.setSender(senderOpt.get());
            message.setMessage(messageText);
            
            ChatMessage savedMessage = messageRepository.save(message);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Solo el staff puede cerrar tickets
    @PutMapping("/conversation/{conversationId}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<ChatConversation> closeConversation(@PathVariable Long conversationId) {
        Optional<ChatConversation> convOpt = conversationRepository.findById(conversationId);
        if (convOpt.isPresent()) {
            ChatConversation conv = convOpt.get();
            conv.setStatus("CLOSED");
            return ResponseEntity.ok(conversationRepository.save(conv));
        }
        return ResponseEntity.notFound().build();
    }
}