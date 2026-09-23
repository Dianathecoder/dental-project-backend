package com.dynalar.dynalar.dto;

import java.time.LocalDateTime;

public class AutoAssignRequest {
    private Long patientId;
    private Long treatmentId;
    private Long doctorId; 
    private LocalDateTime requestedTime;
    private String reason;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }

    public Long getDoctorId() { return doctorId; } // <-- NUEVO
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; } // <-- NUEVO

    public LocalDateTime getRequestedTime() { return requestedTime; }
    public void setRequestedTime(LocalDateTime requestedTime) { this.requestedTime = requestedTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}