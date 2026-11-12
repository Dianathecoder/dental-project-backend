package com.dynalar.dynalar.model.chatmodel;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.dynalar.dynalar.model.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "chat_direct_message",
       indexes = @Index(name = "idx_chat_msg_conv", columnList = "conversation_id,id"))
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private DirectConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // "read" es palabra reservada en MySQL, por eso el nombre de columna explícito
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    public DirectMessage() {}

    public Long getId() { return id; }
    public DirectConversation getConversation() { return conversation; }
    public void setConversation(DirectConversation conversation) { this.conversation = conversation; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
