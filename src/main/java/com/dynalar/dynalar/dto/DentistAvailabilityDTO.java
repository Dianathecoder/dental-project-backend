package com.dynalar.dynalar.dto;

import java.util.List;

public class DentistAvailabilityDTO {

    // --- LUNES ---
    private Boolean mondayMorningActive;
    private String mondayMorningStart;
    private String mondayMorningEnd;
    private Boolean mondayAfternoonActive;
    private String mondayAfternoonStart;
    private String mondayAfternoonEnd;
    private Boolean mondayEveningActive;
    private String mondayEveningStart;
    private String mondayEveningEnd;

    // --- MARTES ---
    private Boolean tuesdayMorningActive;
    private String tuesdayMorningStart;
    private String tuesdayMorningEnd;
    private Boolean tuesdayAfternoonActive;
    private String tuesdayAfternoonStart;
    private String tuesdayAfternoonEnd;
    private Boolean tuesdayEveningActive;
    private String tuesdayEveningStart;
    private String tuesdayEveningEnd;

    // --- MIÉRCOLES ---
    private Boolean wednesdayMorningActive;
    private String wednesdayMorningStart;
    private String wednesdayMorningEnd;
    private Boolean wednesdayAfternoonActive;
    private String wednesdayAfternoonStart;
    private String wednesdayAfternoonEnd;
    private Boolean wednesdayEveningActive;
    private String wednesdayEveningStart;
    private String wednesdayEveningEnd;

    // --- JUEVES ---
    private Boolean thursdayMorningActive;
    private String thursdayMorningStart;
    private String thursdayMorningEnd;
    private Boolean thursdayAfternoonActive;
    private String thursdayAfternoonStart;
    private String thursdayAfternoonEnd;
    private Boolean thursdayEveningActive;
    private String thursdayEveningStart;
    private String thursdayEveningEnd;

    // --- VIERNES ---
    private Boolean fridayMorningActive;
    private String fridayMorningStart;
    private String fridayMorningEnd;
    private Boolean fridayAfternoonActive;
    private String fridayAfternoonStart;
    private String fridayAfternoonEnd;
    private Boolean fridayEveningActive;
    private String fridayEveningStart;
    private String fridayEveningEnd;

    // --- TRATAMIENTOS ---
    private List<Long> treatmentIds;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    // Lunes
    public Boolean getMondayMorningActive() { return mondayMorningActive; }
    public void setMondayMorningActive(Boolean mondayMorningActive) { this.mondayMorningActive = mondayMorningActive; }
    public String getMondayMorningStart() { return mondayMorningStart; }
    public void setMondayMorningStart(String mondayMorningStart) { this.mondayMorningStart = mondayMorningStart; }
    public String getMondayMorningEnd() { return mondayMorningEnd; }
    public void setMondayMorningEnd(String mondayMorningEnd) { this.mondayMorningEnd = mondayMorningEnd; }
    public Boolean getMondayAfternoonActive() { return mondayAfternoonActive; }
    public void setMondayAfternoonActive(Boolean mondayAfternoonActive) { this.mondayAfternoonActive = mondayAfternoonActive; }
    public String getMondayAfternoonStart() { return mondayAfternoonStart; }
    public void setMondayAfternoonStart(String mondayAfternoonStart) { this.mondayAfternoonStart = mondayAfternoonStart; }
    public String getMondayAfternoonEnd() { return mondayAfternoonEnd; }
    public void setMondayAfternoonEnd(String mondayAfternoonEnd) { this.mondayAfternoonEnd = mondayAfternoonEnd; }
    public Boolean getMondayEveningActive() { return mondayEveningActive; }
    public void setMondayEveningActive(Boolean mondayEveningActive) { this.mondayEveningActive = mondayEveningActive; }
    public String getMondayEveningStart() { return mondayEveningStart; }
    public void setMondayEveningStart(String mondayEveningStart) { this.mondayEveningStart = mondayEveningStart; }
    public String getMondayEveningEnd() { return mondayEveningEnd; }
    public void setMondayEveningEnd(String mondayEveningEnd) { this.mondayEveningEnd = mondayEveningEnd; }

    // Martes
    public Boolean getTuesdayMorningActive() { return tuesdayMorningActive; }
    public void setTuesdayMorningActive(Boolean tuesdayMorningActive) { this.tuesdayMorningActive = tuesdayMorningActive; }
    public String getTuesdayMorningStart() { return tuesdayMorningStart; }
    public void setTuesdayMorningStart(String tuesdayMorningStart) { this.tuesdayMorningStart = tuesdayMorningStart; }
    public String getTuesdayMorningEnd() { return tuesdayMorningEnd; }
    public void setTuesdayMorningEnd(String tuesdayMorningEnd) { this.tuesdayMorningEnd = tuesdayMorningEnd; }
    public Boolean getTuesdayAfternoonActive() { return tuesdayAfternoonActive; }
    public void setTuesdayAfternoonActive(Boolean tuesdayAfternoonActive) { this.tuesdayAfternoonActive = tuesdayAfternoonActive; }
    public String getTuesdayAfternoonStart() { return tuesdayAfternoonStart; }
    public void setTuesdayAfternoonStart(String tuesdayAfternoonStart) { this.tuesdayAfternoonStart = tuesdayAfternoonStart; }
    public String getTuesdayAfternoonEnd() { return tuesdayAfternoonEnd; }
    public void setTuesdayAfternoonEnd(String tuesdayAfternoonEnd) { this.tuesdayAfternoonEnd = tuesdayAfternoonEnd; }
    public Boolean getTuesdayEveningActive() { return tuesdayEveningActive; }
    public void setTuesdayEveningActive(Boolean tuesdayEveningActive) { this.tuesdayEveningActive = tuesdayEveningActive; }
    public String getTuesdayEveningStart() { return tuesdayEveningStart; }
    public void setTuesdayEveningStart(String tuesdayEveningStart) { this.tuesdayEveningStart = tuesdayEveningStart; }
    public String getTuesdayEveningEnd() { return tuesdayEveningEnd; }
    public void setTuesdayEveningEnd(String tuesdayEveningEnd) { this.tuesdayEveningEnd = tuesdayEveningEnd; }

    // Miércoles
    public Boolean getWednesdayMorningActive() { return wednesdayMorningActive; }
    public void setWednesdayMorningActive(Boolean wednesdayMorningActive) { this.wednesdayMorningActive = wednesdayMorningActive; }
    public String getWednesdayMorningStart() { return wednesdayMorningStart; }
    public void setWednesdayMorningStart(String wednesdayMorningStart) { this.wednesdayMorningStart = wednesdayMorningStart; }
    public String getWednesdayMorningEnd() { return wednesdayMorningEnd; }
    public void setWednesdayMorningEnd(String wednesdayMorningEnd) { this.wednesdayMorningEnd = wednesdayMorningEnd; }
    public Boolean getWednesdayAfternoonActive() { return wednesdayAfternoonActive; }
    public void setWednesdayAfternoonActive(Boolean wednesdayAfternoonActive) { this.wednesdayAfternoonActive = wednesdayAfternoonActive; }
    public String getWednesdayAfternoonStart() { return wednesdayAfternoonStart; }
    public void setWednesdayAfternoonStart(String wednesdayAfternoonStart) { this.wednesdayAfternoonStart = wednesdayAfternoonStart; }
    public String getWednesdayAfternoonEnd() { return wednesdayAfternoonEnd; }
    public void setWednesdayAfternoonEnd(String wednesdayAfternoonEnd) { this.wednesdayAfternoonEnd = wednesdayAfternoonEnd; }
    public Boolean getWednesdayEveningActive() { return wednesdayEveningActive; }
    public void setWednesdayEveningActive(Boolean wednesdayEveningActive) { this.wednesdayEveningActive = wednesdayEveningActive; }
    public String getWednesdayEveningStart() { return wednesdayEveningStart; }
    public void setWednesdayEveningStart(String wednesdayEveningStart) { this.wednesdayEveningStart = wednesdayEveningStart; }
    public String getWednesdayEveningEnd() { return wednesdayEveningEnd; }
    public void setWednesdayEveningEnd(String wednesdayEveningEnd) { this.wednesdayEveningEnd = wednesdayEveningEnd; }

    // Jueves
    public Boolean getThursdayMorningActive() { return thursdayMorningActive; }
    public void setThursdayMorningActive(Boolean thursdayMorningActive) { this.thursdayMorningActive = thursdayMorningActive; }
    public String getThursdayMorningStart() { return thursdayMorningStart; }
    public void setThursdayMorningStart(String thursdayMorningStart) { this.thursdayMorningStart = thursdayMorningStart; }
    public String getThursdayMorningEnd() { return thursdayMorningEnd; }
    public void setThursdayMorningEnd(String thursdayMorningEnd) { this.thursdayMorningEnd = thursdayMorningEnd; }
    public Boolean getThursdayAfternoonActive() { return thursdayAfternoonActive; }
    public void setThursdayAfternoonActive(Boolean thursdayAfternoonActive) { this.thursdayAfternoonActive = thursdayAfternoonActive; }
    public String getThursdayAfternoonStart() { return thursdayAfternoonStart; }
    public void setThursdayAfternoonStart(String thursdayAfternoonStart) { this.thursdayAfternoonStart = thursdayAfternoonStart; }
    public String getThursdayAfternoonEnd() { return thursdayAfternoonEnd; }
    public void setThursdayAfternoonEnd(String thursdayAfternoonEnd) { this.thursdayAfternoonEnd = thursdayAfternoonEnd; }
    public Boolean getThursdayEveningActive() { return thursdayEveningActive; }
    public void setThursdayEveningActive(Boolean thursdayEveningActive) { this.thursdayEveningActive = thursdayEveningActive; }
    public String getThursdayEveningStart() { return thursdayEveningStart; }
    public void setThursdayEveningStart(String thursdayEveningStart) { this.thursdayEveningStart = thursdayEveningStart; }
    public String getThursdayEveningEnd() { return thursdayEveningEnd; }
    public void setThursdayEveningEnd(String thursdayEveningEnd) { this.thursdayEveningEnd = thursdayEveningEnd; }

    // Viernes
    public Boolean getFridayMorningActive() { return fridayMorningActive; }
    public void setFridayMorningActive(Boolean fridayMorningActive) { this.fridayMorningActive = fridayMorningActive; }
    public String getFridayMorningStart() { return fridayMorningStart; }
    public void setFridayMorningStart(String fridayMorningStart) { this.fridayMorningStart = fridayMorningStart; }
    public String getFridayMorningEnd() { return fridayMorningEnd; }
    public void setFridayMorningEnd(String fridayMorningEnd) { this.fridayMorningEnd = fridayMorningEnd; }
    public Boolean getFridayAfternoonActive() { return fridayAfternoonActive; }
    public void setFridayAfternoonActive(Boolean fridayAfternoonActive) { this.fridayAfternoonActive = fridayAfternoonActive; }
    public String getFridayAfternoonStart() { return fridayAfternoonStart; }
    public void setFridayAfternoonStart(String fridayAfternoonStart) { this.fridayAfternoonStart = fridayAfternoonStart; }
    public String getFridayAfternoonEnd() { return fridayAfternoonEnd; }
    public void setFridayAfternoonEnd(String fridayAfternoonEnd) { this.fridayAfternoonEnd = fridayAfternoonEnd; }
    public Boolean getFridayEveningActive() { return fridayEveningActive; }
    public void setFridayEveningActive(Boolean fridayEveningActive) { this.fridayEveningActive = fridayEveningActive; }
    public String getFridayEveningStart() { return fridayEveningStart; }
    public void setFridayEveningStart(String fridayEveningStart) { this.fridayEveningStart = fridayEveningStart; }
    public String getFridayEveningEnd() { return fridayEveningEnd; }
    public void setFridayEveningEnd(String fridayEveningEnd) { this.fridayEveningEnd = fridayEveningEnd; }

    // ==========================================
    // MÉTODOS DE COMPATIBILIDAD (LEGACY GETTERS/SETTERS)
    // ==========================================

    public Boolean getMondayMorning() { return mondayMorningActive; }
    public void setMondayMorning(Boolean mondayMorning) { this.mondayMorningActive = mondayMorning; }

    public Boolean getMondayAfternoon() { return mondayAfternoonActive; }
    public void setMondayAfternoon(Boolean mondayAfternoon) { this.mondayAfternoonActive = mondayAfternoon; }

    public Boolean getTuesdayMorning() { return tuesdayMorningActive; }
    public void setTuesdayMorning(Boolean tuesdayMorning) { this.tuesdayMorningActive = tuesdayMorning; }

    public Boolean getTuesdayAfternoon() { return tuesdayAfternoonActive; }
    public void setTuesdayAfternoon(Boolean tuesdayAfternoon) { this.tuesdayAfternoonActive = tuesdayAfternoon; }

    public Boolean getWednesdayMorning() { return wednesdayMorningActive; }
    public void setWednesdayMorning(Boolean wednesdayMorning) { this.wednesdayMorningActive = wednesdayMorning; }

    public Boolean getWednesdayAfternoon() { return wednesdayAfternoonActive; }
    public void setWednesdayAfternoon(Boolean wednesdayAfternoon) { this.wednesdayAfternoonActive = wednesdayAfternoon; }

    public Boolean getThursdayMorning() { return thursdayMorningActive; }
    public void setThursdayMorning(Boolean thursdayMorning) { this.thursdayMorningActive = thursdayMorning; }

    public Boolean getThursdayAfternoon() { return thursdayAfternoonActive; }
    public void setThursdayAfternoon(Boolean thursdayAfternoon) { this.thursdayAfternoonActive = thursdayAfternoon; }

    public Boolean getFridayMorning() { return fridayMorningActive; }
    public void setFridayMorning(Boolean fridayMorning) { this.fridayMorningActive = fridayMorning; }

    public Boolean getFridayAfternoon() { return fridayAfternoonActive; }
    public void setFridayAfternoon(Boolean fridayAfternoon) { this.fridayAfternoonActive = fridayAfternoon; }

    // Tratamientos
    public List<Long> getTreatmentIds() { return treatmentIds; }
    public void setTreatmentIds(List<Long> treatmentIds) { this.treatmentIds = treatmentIds; }
}