package com.dynalar.dynalar.respository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dynalar.dynalar.model.chatmodel.DirectMessage;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

    /** Carga inicial: los últimos 100 (vienen del más nuevo al más viejo). */
    List<DirectMessage> findTop100ByConversationIdOrderByIdDesc(Long conversationId);

    /** Polling: solo lo que llegó después del último mensaje que ya tiene el cliente. */
    List<DirectMessage> findByConversationIdAndIdGreaterThanOrderByIdAsc(Long conversationId, Long afterId);

    @Modifying(clearAutomatically = true)
    @Query("update DirectMessage m set m.read = true " +
           "where m.conversation.id = :cid and m.sender.id <> :uid and m.read = false")
    int markRead(@Param("cid") Long conversationId, @Param("uid") Long readerId);

    @Query("select count(m) from DirectMessage m " +
           "where m.conversation.id = :cid and m.sender.id <> :uid and m.read = false")
    long countUnread(@Param("cid") Long conversationId, @Param("uid") Long userId);

    @Query("select count(m) from DirectMessage m " +
           "where (m.conversation.userLow.id = :uid or m.conversation.userHigh.id = :uid) " +
           "and m.sender.id <> :uid and m.read = false")
    long countAllUnread(@Param("uid") Long userId);
}