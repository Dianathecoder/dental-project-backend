package com.dynalar.dynalar.dto;

import java.time.LocalDateTime;

public class AutoAssignRequest {
    private Long patientId;
    private Long treatmentId;
    private LocalDateTime requestedTime;

    public AutoAssignRequest() {}

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }
    public LocalDateTime getRequestedTime() { return requestedTime; }
    public void setRequestedTime(LocalDateTime requestedTime) { this.requestedTime = requestedTime; }
}