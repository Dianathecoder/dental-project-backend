package com.dynalar.dynalar.model.chatmodel;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.dynalar.dynalar.model.user.User;

import jakarta.persistence.*;

/**
 * Conversación 1 a 1 entre dos usuarios.
 * Siempre se guarda con userLow.id < userHigh.id, así el par (A,B) y (B,A)
 * es la MISMA conversación y la constraint única evita duplicados.
 */
@Entity
@Table(name = "chat_direct_conversation",
       uniqueConstraints = @UniqueConstraint(name = "uk_chat_pair", columnNames = {"user_low_id", "user_high_id"}))
public class DirectConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_low_id", nullable = false)
    private User userLow;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_high_id", nullable = false)
    private User userHigh;

    private LocalDateTime lastMessageAt;

    @Column(length = 200)
    private String lastMessagePreview;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public DirectConversation() {}

    public Long getId() { return id; }
    public User getUserLow() { return userLow; }
    public void setUserLow(User userLow) { this.userLow = userLow; }
    public User getUserHigh() { return userHigh; }
    public void setUserHigh(User userHigh) { this.userHigh = userHigh; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public String getLastMessagePreview() { return lastMessagePreview; }
    public void setLastMessagePreview(String lastMessagePreview) { this.lastMessagePreview = lastMessagePreview; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
