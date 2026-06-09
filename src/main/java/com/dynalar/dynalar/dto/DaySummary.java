package com.dynalar.dynalar.dto;

public class DaySummary {

	private String date;
	private boolean hasAppointments;
	private boolean hasinfectiousPatient;
	
	public DaySummary(String date, boolean hasAppointments, boolean hasinfeciousPatient) {
		this.date = date;
		this.hasAppointments = hasAppointments;
		this.hasinfectiousPatient = hasinfeciousPatient;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isHasAppointments() {
		return hasAppointments;
	}

	public void setHasAppointments(boolean hasAppointments) {
		this.hasAppointments = hasAppointments;
	}

	public boolean isHasinfeciousPatient() {
		return hasinfectiousPatient;
	}

	public void setHasinfeciousPatient(boolean hasinfeciousPatient) {
		this.hasinfectiousPatient = hasinfeciousPatient;
	}
	
}
