package com.dynalar.dynalar.respository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByDentistIdAndStartTimeBetween(Long dentistId, LocalDateTime start, LocalDateTime end);
	List<Appointment> findByBox_NumberAndStartTimeBetween(Integer boxNumber, LocalDateTime start, LocalDateTime end);
	List<Appointment> findByTreatment_IdAndBox_NumberAndStartTimeBetween(Long treatmentId, Integer boxNumber, LocalDateTime start, LocalDateTime end);
	List<Appointment> findByTreatment_IdAndStartTimeBetween(Long patientId, LocalDateTime start, LocalDateTime end);
	boolean existsByBox_NumberAndStartTimeAfter(Integer boxNumber, LocalDateTime start);
	Page<Appointment> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
	List<Appointment> findByPatientId(Long patientId);

	@Query(value = """
		    SELECT
		        DATE(a.start_time) as date,
		        COUNT(*) > 0 as hasAppointment,
		        MAX(CASE WHEN mr.infectious_deceases IS NOT NULL AND mr.infectious_deceases != '' THEN 1 ELSE 0 END) as hasInfectiousPatient
		    FROM appointment a
		    LEFT JOIN patient p ON a.patient_id = p.id
		    LEFT JOIN medical_record mr ON p.medical_record_id = mr.id
		    WHERE a.start_time BETWEEN :start AND :end
		    GROUP BY DATE(a.start_time)
		""", nativeQuery = true)
	List<Object[]> findSummaryNative(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
