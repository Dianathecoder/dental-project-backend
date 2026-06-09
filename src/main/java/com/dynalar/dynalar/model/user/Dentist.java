package com.dynalar.dynalar.model.user;

import java.util.Set;

import com.dynalar.dynalar.model.Treatment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "dentist")
public class Dentist extends User {

	private Boolean mondayMorning;
	private Boolean mondayAfternoon;
	private Boolean tuesdayMorning;
	private Boolean tuesdayAfternoon;
	private Boolean wednesdayMorning;
	private Boolean wednesdayAfternoon;
	private Boolean thursdayMorning;
	private Boolean thursdayAfternoon;
	private Boolean fridayMorning;
	private Boolean fridayAfternoon;

	@ManyToMany
	@JoinTable(name = "dentist_treatment", joinColumns = @JoinColumn(name = "dentist_id"), inverseJoinColumns = @JoinColumn(name = "treatment_id"))
	@JsonIgnore
	private Set<Treatment> treatments;

	public Dentist() {

	}

	public Boolean getMondayMorning() {
		return mondayMorning;
	}

	public void setMondayMorning(Boolean mondayMorning) {
		this.mondayMorning = mondayMorning;
	}

	public Boolean getMondayAfternoon() {
		return mondayAfternoon;
	}

	public void setMondayAfternoon(Boolean mondayAfternoon) {
		this.mondayAfternoon = mondayAfternoon;
	}

	public Boolean getTuesdayMorning() {
		return tuesdayMorning;
	}

	public void setTuesdayMorning(Boolean tuesdayMorning) {
		this.tuesdayMorning = tuesdayMorning;
	}

	public Boolean getTuesdayAfternoon() {
		return tuesdayAfternoon;
	}

	public void setTuesdayAfternoon(Boolean tuesdayAfternoon) {
		this.tuesdayAfternoon = tuesdayAfternoon;
	}

	public Boolean getWednesdayMorning() {
		return wednesdayMorning;
	}

	public void setWednesdayMorning(Boolean wednesdayMorning) {
		this.wednesdayMorning = wednesdayMorning;
	}

	public Boolean getWednesdayAfternoon() {
		return wednesdayAfternoon;
	}

	public void setWednesdayAfternoon(Boolean wednesdayAfternoon) {
		this.wednesdayAfternoon = wednesdayAfternoon;
	}

	public Boolean getThursdayMorning() {
		return thursdayMorning;
	}

	public void setThursdayMorning(Boolean thursdayMorning) {
		this.thursdayMorning = thursdayMorning;
	}

	public Boolean getThursdayAfternoon() {
		return thursdayAfternoon;
	}

	public void setThursdayAfternoon(Boolean thursdayAfternoon) {
		this.thursdayAfternoon = thursdayAfternoon;
	}

	public Boolean getFridayMorning() {
		return fridayMorning;
	}

	public void setFridayMorning(Boolean fridayMorning) {
		this.fridayMorning = fridayMorning;
	}

	public Boolean getFridayAfternoon() {
		return fridayAfternoon;
	}

	public void setFridayAfternoon(Boolean fridayAfternoon) {
		this.fridayAfternoon = fridayAfternoon;
	}

	public Set<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(Set<Treatment> treatments) {
		this.treatments = treatments;
	}
}
