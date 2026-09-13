package com.dynalar.dynalar.model.staffClock.DTO;

public class AbsenceResponseDTO {
    private Long id;
    private String title;
    private String staffName;
    private String type;
    private String startDate;
    private String endDate;
    private String avatarUrl;

    public AbsenceResponseDTO(Long id, String title, String staffName, String type, String startDate, String endDate, String avatarUrl) {
        this.id = id;
        this.title = title;
        this.staffName = staffName;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.avatarUrl = avatarUrl;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}