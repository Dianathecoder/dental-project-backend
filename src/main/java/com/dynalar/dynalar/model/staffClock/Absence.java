package com.dynalar.dynalar.model.staffClock;

import com.dynalar.dynalar.model.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Null si es festivo general
    private User user;

    private String title;

    @Enumerated(EnumType.STRING)
    private AbsenceType type;

    private LocalDate startDate;
    private LocalDate endDate;

    // Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public AbsenceType getType() { return type; }
    public void setType(AbsenceType type) { this.type = type; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}

