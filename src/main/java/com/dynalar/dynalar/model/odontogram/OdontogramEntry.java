package com.dynalar.dynalar.model.odontogram;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "odontogram_entry")
public class OdontogramEntry {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JsonIgnore
	    private Odontogram odontogram;

	    @ManyToOne
	    private Tooth tooth;

	    @Enumerated(EnumType.STRING)
	    @Column(name = "surface", length = 30)
	    private ToothSurface surface;
	    
	    @ManyToOne
	    private DentalProcess dentalProcess;
	    
	    @Enumerated(EnumType.STRING)
	    @Column(name = "process_status", length = 30)
	    private ProcessStatus processStatus;
	    
	    
	    public OdontogramEntry() {
	    }

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Odontogram getOdontogram() {
			return odontogram;
		}

		public void setOdontogram(Odontogram odontogram) {
			this.odontogram = odontogram;
		}

		public Tooth getTooth() {
			return tooth;
		}

		public void setTooth(Tooth tooth) {
			this.tooth = tooth;
		}

		public ToothSurface getSurface() {
			return surface;
		}

		public void setSurface(ToothSurface surface) {
			this.surface = surface;
		}


		public DentalProcess getDentalProcess() {
			return dentalProcess;
		}

		public void setDentalProcess(DentalProcess dentalProcess) {
			this.dentalProcess = dentalProcess;
		}

		public ProcessStatus getProcessStatus() {
			return processStatus;
		}
		
		public void setProcessStatus(ProcessStatus processStatus) {
			this.processStatus = processStatus;
		}
}
