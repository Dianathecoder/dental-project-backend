package com.dynalar.dynalar.dto.chat;

import java.time.LocalDateTime;
import java.util.List;
 
public final class ChatDtos {
 
    private ChatDtos() {}
 
    public record ContactDTO(
            Long id,
            String name,
            String surname,
            String avatarUrl,
            String role,           
            List<String> roles,
            String lastMessage,
            LocalDateTime lastMessageAt,
            long unread,
            Long patientId) {}
 
    public record MessageDTO(
            Long id,
            Long senderId,
            String text,
            LocalDateTime createdAt,
            boolean mine,          
            boolean read) {}
 
    public record ConversationDTO(ContactDTO contact, List<MessageDTO> messages) {}
 
    public record SendMessageRequest(String text) {}
}
 
