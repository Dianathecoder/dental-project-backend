package com.dynalar.dynalar.controller;

import java.util.List;
import java.util.Map;
 
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.dynalar.dynalar.dto.chat.ChatDtos.ContactDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.ConversationDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.MessageDTO;
import com.dynalar.dynalar.dto.chat.ChatDtos.SendMessageRequest;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.UserRepository;
import com.dynalar.dynalar.service.ChatService;
import com.dynalar.dynalar.service.ChatScope;
 
@RestController
@RequestMapping("/api/chat")
public class ChatController {
 
    private final ChatService chatService;
    private final UserRepository userRepo;
 
    public ChatController(ChatService chatService, UserRepository userRepo) {
        this.chatService = chatService;
        this.userRepo = userRepo;
    }
 
    /** Con quién puedo hablar (ya filtrado por rol) + último mensaje + no leídos. */
    @GetMapping("/contacts")
    public List<ContactDTO> contacts(Authentication auth,
                                     @RequestParam(required = false) String q,
                                     @RequestParam(defaultValue = "STAFF") ChatScope scope) {
        return chatService.contacts(current(auth), q, scope);
    }
 
    /** Abre la conversación. ?after=<id> devuelve solo mensajes nuevos (polling). */
    @GetMapping("/with/{userId}")
    public ConversationDTO conversation(Authentication auth,
                                        @PathVariable Long userId,
                                        @RequestParam(required = false) Long after,
                                        @RequestParam(defaultValue = "STAFF") ChatScope scope) {
        return chatService.open(current(auth), userId, after, scope);
    }
 
    @PostMapping("/with/{userId}")
    public MessageDTO send(Authentication auth,
                           @PathVariable Long userId,
                           @RequestParam(defaultValue = "STAFF") ChatScope scope,
                           @RequestBody SendMessageRequest body) {
        return chatService.send(current(auth), userId, body.text(), scope);
    }
 
    @GetMapping("/unread-count")
    public Map<String, Long> unread(Authentication auth) {
        return Map.of("count", chatService.unreadCount(current(auth)));
    }
 
    // El JWT lleva el email como subject (igual que en logout)
    private User current(Authentication auth) {
        if (auth == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return userRepo.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
 
