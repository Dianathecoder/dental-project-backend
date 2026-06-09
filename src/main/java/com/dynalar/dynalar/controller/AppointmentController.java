package com.dynalar.dynalar.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

import com.dynalar.dynalar.dto.AutoAssignRequest;
import com.dynalar.dynalar.dto.DaySummary;
import com.dynalar.dynalar.model.Appointment;
import com.dynalar.dynalar.model.Box;
import com.dynalar.dynalar.respository.AppointmentRepository;
import com.dynalar.dynalar.respository.BoxRepository;
import com.dynalar.dynalar.respository.DentistRepository;
import com.dynalar.dynalar.respository.PatientRepository;
import com.dynalar.dynalar.respository.TreatmentRepository;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private TreatmentRepository treatmentRepository;

	@Autowired
	private DentistRepository dentistRepository;

	@Autowired
	private BoxRepository boxRepository;

	@PostMapping("/auto-assign")
	public ResponseEntity<?> autoAssignAppointment(@RequestBody AutoAssignRequest request) {
		try {
			com.dynalar.dynalar.model.patient.Patient patient = patientRepository.findById(request.getPatientId()).orElse(null);
			com.dynalar.dynalar.model.Treatment treatment = treatmentRepository.findById(request.getTreatmentId()).orElse(null);
			
			if (patient == null || treatment == null) {
				return ResponseEntity.badRequest().body("Paciente o Tratamiento no encontrado.");
			}

			LocalDateTime requestedStart = request.getRequestedTime();
			int duration = treatment.getDurationMinutes();
			LocalDateTime requestedEnd = requestedStart.plusMinutes(duration);
			LocalDateTime requestedEndWithCleaning = requestedEnd.plusMinutes(15);

			java.time.LocalTime morningStart = java.time.LocalTime.of(9, 0);
			java.time.LocalTime morningEnd = java.time.LocalTime.of(14, 0);
			java.time.LocalTime afternoonStart = java.time.LocalTime.of(15, 0);
			java.time.LocalTime afternoonEnd = java.time.LocalTime.of(20, 0);

			java.time.LocalTime reqStartStr = requestedStart.toLocalTime();
			java.time.LocalTime reqEndStr = requestedEndWithCleaning.toLocalTime();

			boolean isMorning = (reqStartStr.compareTo(morningStart) >= 0 && reqEndStr.compareTo(morningEnd) <= 0);
			boolean isAfternoon = (reqStartStr.compareTo(afternoonStart) >= 0 && reqEndStr.compareTo(afternoonEnd) <= 0);

			if (!isMorning && !isAfternoon) {
				return ResponseEntity.badRequest().body("La cita más el tiempo de limpieza se sale del horario laboral de la clínica.");
			}

			List<com.dynalar.dynalar.model.user.Dentist> qualifiedDentists = dentistRepository.findByTreatments_Id(treatment.getId());

			com.dynalar.dynalar.model.user.Dentist selectedDentist = null;
			java.time.DayOfWeek dayOfWeek = requestedStart.getDayOfWeek();

			for (com.dynalar.dynalar.model.user.Dentist dentist : qualifiedDentists) {
				boolean worksShift = false;
				switch (dayOfWeek) {
					case MONDAY: worksShift = isMorning ? Boolean.TRUE.equals(dentist.getMondayMorning()) : Boolean.TRUE.equals(dentist.getMondayAfternoon()); break;
					case TUESDAY: worksShift = isMorning ? Boolean.TRUE.equals(dentist.getTuesdayMorning()) : Boolean.TRUE.equals(dentist.getTuesdayAfternoon()); break;
					case WEDNESDAY: worksShift = isMorning ? Boolean.TRUE.equals(dentist.getWednesdayMorning()) : Boolean.TRUE.equals(dentist.getWednesdayAfternoon()); break;
					case THURSDAY: worksShift = isMorning ? Boolean.TRUE.equals(dentist.getThursdayMorning()) : Boolean.TRUE.equals(dentist.getThursdayAfternoon()); break;
					case FRIDAY: worksShift = isMorning ? Boolean.TRUE.equals(dentist.getFridayMorning()) : Boolean.TRUE.equals(dentist.getFridayAfternoon()); break;
					default: worksShift = false;
				}

				if (worksShift) {
					LocalDateTime startOfDay = requestedStart.toLocalDate().atStartOfDay();
					LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
					
					List<Appointment> existingAppointments = appointmentRepository.findByDentistIdAndStartTimeBetween(dentist.getId(), startOfDay, endOfDay);
					
					boolean hasOverlap = false;
					for (Appointment app : existingAppointments) {
						LocalDateTime appEndWithCleaning = app.getEndTime().plusMinutes(15);
						
						if (requestedStart.isBefore(appEndWithCleaning) && requestedEndWithCleaning.isAfter(app.getStartTime())) {
							hasOverlap = true;
							break;
						}
					}

					if (!hasOverlap) {
						selectedDentist = dentist;
						break;
					}
				}
			}

			if (selectedDentist == null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Sin Doctores Disponibles el dia y hora seleccionada. Pruebe otro dia u otra hora.");
			}

			// Verifica que hay un box libre para esa hora
			LocalDateTime startOfDay = requestedStart.toLocalDate().atStartOfDay();
			LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
			List<Box> allBoxes = (List<Box>) boxRepository.findAll();
	        Box selectedBox = null;

	        for (Box box : allBoxes) {
	            List<Appointment> boxApps = appointmentRepository.findByBox_NumberAndStartTimeBetween(box.getNumber(), startOfDay, endOfDay);
	            boolean boxOverlap = false;
	            for (Appointment app : boxApps) {
	                LocalDateTime appEndWithCleaning = app.getEndTime().plusMinutes(15);
	                if (requestedStart.isBefore(appEndWithCleaning) && requestedEndWithCleaning.isAfter(app.getStartTime())) {
	                    boxOverlap = true;
	                    break;
	                }
	            }
	            if (!boxOverlap) {
	                selectedBox = box;
	                break;
	            }
	        }

	        if (selectedBox == null) {
	            return ResponseEntity.status(HttpStatus.CONFLICT).body("No hay Boxes (sillones) disponibles para esta hora.");
	        }
			
			Appointment newAppointment = new Appointment();
			newAppointment.setPatient(patient);
			newAppointment.setTreatment(treatment);
			newAppointment.setDentist(selectedDentist);
			newAppointment.setDurationMinutes(duration);
			newAppointment.setBox(selectedBox);
			newAppointment.setStartTime(requestedStart);
			newAppointment.setEndTime(requestedEnd);
			newAppointment.setReason("Cita Auto-Agendada");

			Appointment savedAppointment = appointmentRepository.save(newAppointment);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno procesando la cita.");
		}
	}


	@GetMapping("/index")
	public @ResponseBody ResponseEntity<Page<Appointment>> getAllAppointments(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start, 
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
			if (start != null && end != null) {
				return ResponseEntity.ok(appointmentRepository.findByStartTimeBetween(start, end, pageable));
			}
			return ResponseEntity.ok(appointmentRepository.findAll(pageable));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(404).build();
		}
	}
	
	@GetMapping("/patient/{patientId}")
	public @ResponseBody ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
		try {
			List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
			return ResponseEntity.ok(appointments);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(404).build();
		}
	}
	
	@GetMapping("/calendar-summary")
	public @ResponseBody ResponseEntity<List<DaySummary>> getCalendarSummary(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

		List<Object[]> results = appointmentRepository.findSummaryNative(start, end);
		List<DaySummary> summary = results.stream().map(row -> new DaySummary(
				           row[0].toString(),                               
				           ((Number) row[1]).longValue() > 0,               
				           ((Number) row[2]).intValue() > 0                
				       )).toList();

		return ResponseEntity.ok(summary);
	}
	
	@PostMapping()
	public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
		try {
			Appointment newAppointment = appointmentRepository.save(appointment);
			return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
		try {
			Appointment appointment = appointmentRepository.findById(id).orElse(null);
			if (appointment == null) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(appointment);
		} catch (Exception e) {
			return ResponseEntity.status(404).build();
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Appointment> updateAppointment(@RequestBody Appointment updatedAppointment) {
		try {
			Long id = updatedAppointment.getId();
			if (id == null) {
				return ResponseEntity.badRequest().build();
			}

			Optional<Appointment> existingOpt = appointmentRepository.findById(id);
			if (existingOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Appointment existingAppointment = existingOpt.get();

			existingAppointment.setReason(updatedAppointment.getReason());
			existingAppointment.setDurationMinutes(updatedAppointment.getDurationMinutes());
			existingAppointment.setStartTime(updatedAppointment.getStartTime());
			existingAppointment.setEndTime(updatedAppointment.getEndTime());

			if (updatedAppointment.getTreatment() != null) {
				existingAppointment.setTreatment(updatedAppointment.getTreatment());
			}
			if (updatedAppointment.getDentist() != null) {
				existingAppointment.setDentist(updatedAppointment.getDentist());
			}
			if (updatedAppointment.getPatient() != null) {
				existingAppointment.setPatient(updatedAppointment.getPatient());
			}

			Appointment savedAppointment = appointmentRepository.save(existingAppointment);
			return ResponseEntity.ok(savedAppointment);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(404).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
		try {
			Optional<Appointment> appointment = appointmentRepository.findById(id);
			if (appointment.isPresent()) {
				appointmentRepository.deleteById(id);
				return ResponseEntity.noContent().build();
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(404).build();
		}
	}

	@PostMapping("/available-slots")
	public ResponseEntity<?> getAvailableSlots(@RequestBody com.dynalar.dynalar.dto.SlotRequest request) {
		try {
			if (request.getTreatmentId() == null || request.getPatientId() == null) {
				return ResponseEntity.badRequest().body("El ID del tratamiento y paciente son requeridos.");
			}

			com.dynalar.dynalar.model.Treatment treatment = treatmentRepository.findById(request.getTreatmentId()).orElse(null);
			if (treatment == null) {
				return ResponseEntity.badRequest().body("Tratamiento no encontrado.");
			}
			  
			com.dynalar.dynalar.model.patient.Patient patient = patientRepository.findById(request.getPatientId()).orElse(null);
			if (patient == null) {
				return ResponseEntity.badRequest().body("Paciente no encontrado.");
			}

			boolean hasInfectiousDisease = false;
			if (patient.getMedicalRecord() != null && patient.getMedicalRecord().getInfectiousDeceases() != null) {
				hasInfectiousDisease = !patient.getMedicalRecord().getInfectiousDeceases().trim().isEmpty();
			}
			
			int totalDuration = treatment.getDurationMinutes() + 15;
			List<com.dynalar.dynalar.model.user.Dentist> qualifiedDentists = dentistRepository.findByTreatments_Id(treatment.getId());
			List<Box> allBoxes = (List<Box>) boxRepository.findAll();

			java.util.Map<String, java.util.List<String>> availableSlotsPerDay = new java.util.TreeMap<>();

			java.time.LocalDate currentDate = request.getStartDate();
			while (!currentDate.isAfter(request.getEndDate())) {
				java.time.DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
				java.util.List<String> dailySlots = new java.util.ArrayList<>();
				
				LocalDateTime startOfDay = currentDate.atStartOfDay();
				LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

				java.util.Map<Long, List<Appointment>> dentistAppsMap = new java.util.HashMap<>();
				for (com.dynalar.dynalar.model.user.Dentist d : qualifiedDentists) {
					dentistAppsMap.put(d.getId(), appointmentRepository.findByDentistIdAndStartTimeBetween(d.getId(), startOfDay, endOfDay));
				}
				java.util.Map<Integer, List<Appointment>> boxAppsMap = new java.util.HashMap<>();
				for (Box b : allBoxes) {
					boxAppsMap.put(b.getNumber(), appointmentRepository.findByBox_NumberAndStartTimeBetween(b.getNumber(), startOfDay, endOfDay));
				}
				List<Appointment> treatmentAppsToday = appointmentRepository.findByTreatment_IdAndStartTimeBetween(treatment.getId(), startOfDay, endOfDay);

				java.time.LocalTime[] possibleTimesArray = {
					java.time.LocalTime.of(9, 0), java.time.LocalTime.of(9, 30), java.time.LocalTime.of(10, 0), java.time.LocalTime.of(10, 30),
					java.time.LocalTime.of(11, 0), java.time.LocalTime.of(11, 30), java.time.LocalTime.of(12, 0), java.time.LocalTime.of(12, 30),
					java.time.LocalTime.of(13, 0), java.time.LocalTime.of(13, 30),
					java.time.LocalTime.of(15, 0), java.time.LocalTime.of(15, 30), java.time.LocalTime.of(16, 0), java.time.LocalTime.of(16, 30),
					java.time.LocalTime.of(17, 0), java.time.LocalTime.of(17, 30), java.time.LocalTime.of(18, 0), java.time.LocalTime.of(18, 30),
					java.time.LocalTime.of(19, 0), java.time.LocalTime.of(19, 30)
				};

				java.util.List<java.time.LocalTime> timesToCheck = new java.util.ArrayList<>(java.util.Arrays.asList(possibleTimesArray));
				
				if (hasInfectiousDisease) {
					java.util.Collections.reverse(timesToCheck);
				}

				for (java.time.LocalTime slotTime : timesToCheck) {
					java.time.LocalDateTime slotStart = LocalDateTime.of(currentDate, slotTime);
					java.time.LocalDateTime slotEnd = slotStart.plusMinutes(totalDuration);
					
					boolean isMorningSlot = slotStart.getHour() < 14 && slotEnd.toLocalTime().compareTo(java.time.LocalTime.of(14, 0)) <= 0;
					boolean isAfternoonSlot = slotStart.getHour() >= 15 && slotEnd.toLocalTime().compareTo(java.time.LocalTime.of(20, 0)) <= 0;

					if (!isMorningSlot && !isAfternoonSlot) continue;

					boolean isSlotAvailable = false;

					for (com.dynalar.dynalar.model.user.Dentist dentist : qualifiedDentists) {
						boolean worksShift = false;
						switch (dayOfWeek) {
							case MONDAY: worksShift = isMorningSlot ? Boolean.TRUE.equals(dentist.getMondayMorning()) : Boolean.TRUE.equals(dentist.getMondayAfternoon()); break;
							case TUESDAY: worksShift = isMorningSlot ? Boolean.TRUE.equals(dentist.getTuesdayMorning()) : Boolean.TRUE.equals(dentist.getTuesdayAfternoon()); break;
							case WEDNESDAY: worksShift = isMorningSlot ? Boolean.TRUE.equals(dentist.getWednesdayMorning()) : Boolean.TRUE.equals(dentist.getWednesdayAfternoon()); break;
							case THURSDAY: worksShift = isMorningSlot ? Boolean.TRUE.equals(dentist.getThursdayMorning()) : Boolean.TRUE.equals(dentist.getThursdayAfternoon()); break;
							case FRIDAY: worksShift = isMorningSlot ? Boolean.TRUE.equals(dentist.getFridayMorning()) : Boolean.TRUE.equals(dentist.getFridayAfternoon()); break;
							default: worksShift = false;
						}

						if (worksShift) {
							List<Appointment> existingApps = dentistAppsMap.get(dentist.getId());
							boolean hasOverlap = false;
							for (Appointment app : existingApps) {
								LocalDateTime appEndWithCleaning = app.getEndTime().plusMinutes(15);
								if (slotStart.isBefore(appEndWithCleaning) && slotEnd.isAfter(app.getStartTime())) {
									hasOverlap = true;
									break;
								}
							}
							if (!hasOverlap) {
								isSlotAvailable = true;
								break; 
							}
						}
					}

					if (isSlotAvailable) {
					    boolean hasAvailableBox = false;
					    for (Box box : allBoxes) {
					        List<Appointment> boxApps = boxAppsMap.get(box.getNumber());
					        boolean boxOverlap = false;
					        boolean hasAppsAfter = false;

					        for (Appointment app : boxApps) {
					            LocalDateTime appEndWithCleaning = app.getEndTime().plusMinutes(15);
					            if (slotStart.isBefore(appEndWithCleaning) && slotEnd.isAfter(app.getStartTime())) {
					                boxOverlap = true;
					            }
					            if (hasInfectiousDisease && app.getStartTime().isAfter(slotStart)) {
					                hasAppsAfter = true;
					            }
					        }

					        if (!boxOverlap && !hasAppsAfter) {
					            boolean hasSameTreatmentAfter = false;
					            if (hasInfectiousDisease) {
					                for (Appointment tApp : treatmentAppsToday) {
					                    if (tApp.getStartTime().isAfter(slotStart)) {
					                        hasSameTreatmentAfter = true;
					                        break;
					                    }
					                }
					            }

					            if (!hasSameTreatmentAfter) {
					                hasAvailableBox = true;
					                break;
					            }
					        }
					    }
					    
					    if (hasAvailableBox) {
					        dailySlots.add(String.format("%02d:%02d", slotTime.getHour(), slotTime.getMinute()));
					        
					        if (hasInfectiousDisease) {
					            break;
					        }
					    }
					}
				}

				if (!dailySlots.isEmpty()) {
					java.util.Collections.sort(dailySlots);
					availableSlotsPerDay.put(currentDate.toString(), dailySlots);
				}
				currentDate = currentDate.plusDays(1);
			}			

			return ResponseEntity.ok(availableSlotsPerDay);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculando los huecos.");
		}
	}
}