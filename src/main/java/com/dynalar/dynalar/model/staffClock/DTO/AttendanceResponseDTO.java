package com.dynalar.dynalar.model.staffClock.DTO;
import java.util.List;

public class AttendanceResponseDTO {
    private Long id;
    private String staffName;
    private String role;
    private List<String> roles;
    private String sex;
    private String date;
    private String checkInTime;
    private String checkOutTime;
    private String avatarUrl;
    
    public AttendanceResponseDTO(Long id, String staffName, String role, List<String> roles, String sex, String date, String checkInTime, String checkOutTime, String avatarUrl) {
        this.id = id;
        this.staffName = staffName;
        this.role = role;
        this.roles = roles;
        this.sex = sex;
        this.date = date;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.avatarUrl = avatarUrl;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }
    public String getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(String checkOutTime) { this.checkOutTime = checkOutTime; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}