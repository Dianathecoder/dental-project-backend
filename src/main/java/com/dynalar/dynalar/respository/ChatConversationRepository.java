package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dynalar.dynalar.ChatConversation.ChatConversation;

import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    List<ChatConversation> findByPatientId(Long patientId);
    List<ChatConversation> findByStatus(String status);
}