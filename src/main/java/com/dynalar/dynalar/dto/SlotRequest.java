package com.dynalar.dynalar.dto;

import java.time.LocalDate;

public class SlotRequest {
    private Long patientId;
    private Long treatmentId;
    private Long doctorId; 

    private LocalDate startDate;
    private LocalDate endDate;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getTreatmentId() { return treatmentId; }
    public void setTreatmentId(Long treatmentId) { this.treatmentId = treatmentId; }

    public Long getDoctorId() { return doctorId; } // <-- NUEVO
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; } // <-- NUEVO

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}