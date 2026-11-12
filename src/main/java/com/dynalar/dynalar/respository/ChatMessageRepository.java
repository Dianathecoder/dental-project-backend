package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dynalar.dynalar.chatConversation.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}