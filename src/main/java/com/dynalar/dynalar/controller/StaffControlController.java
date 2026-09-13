package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.model.staffClock.Absence;
import com.dynalar.dynalar.model.staffClock.AbsenceType;
import com.dynalar.dynalar.model.staffClock.Attendance;
import com.dynalar.dynalar.model.staffClock.DTO.AbsenceRequestDTO;
import com.dynalar.dynalar.model.staffClock.DTO.AbsenceResponseDTO;
import com.dynalar.dynalar.model.staffClock.DTO.AttendanceResponseDTO;
import com.dynalar.dynalar.model.staffClock.DTO.ClockRequestDTO;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.AbsenceRepository;
import com.dynalar.dynalar.respository.AttendanceRepository;
import com.dynalar.dynalar.respository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/staff-control", "/api/staff-control"})
public class StaffControlController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AbsenceRepository absenceRepository;

    @GetMapping("/daily")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_OWNER', 'ROLE_ADMIN', 'ROLE_AUXILIAR')")
    public ResponseEntity<List<AttendanceResponseDTO>> getDailyAttendance(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        
        List<User> staffMembers = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().noneMatch(r -> r.toString().contains("PATIENT")))
                .collect(Collectors.toList());

        List<AttendanceResponseDTO> response = new ArrayList<>();

        for (User user : staffMembers) {
            Attendance attendance = attendanceRepository.findByUserAndDate(user, localDate).orElse(null);
            String roleName = user.getRoles().isEmpty() ? "Personal" : user.getRoles().iterator().next().toString();
            List<String> roleList = user.getRoles().stream()
                    .map(r -> r.toString().startsWith("ROLE_") ? r.toString() : "ROLE_" + r.toString())
                    .collect(Collectors.toList());
                    
            String checkIn = (attendance != null && attendance.getCheckInTime() != null) 
                    ? attendance.getCheckInTime().toString() : null;
            String sexValue = user.getSex() != null ? user.getSex().toString() : "OTHER";
            String checkOut = (attendance != null && attendance.getCheckOutTime() != null) 
                    ? attendance.getCheckOutTime().toString() : null;
                    
            response.add(new AttendanceResponseDTO(
                    user.getId(),
                    user.getName() + " " + user.getSurname(),
                    roleName,
                    roleList,
                    sexValue,
                    date,
                    checkIn,
                    checkOut,
                    user.getAvatarUrl()
            ));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping({"/monthly-absences", "/absences/monthly"})
    public ResponseEntity<List<AbsenceResponseDTO>> getMonthlyAbsences(
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        LocalDate start;
        LocalDate end;

        if (yearMonth != null && !yearMonth.isEmpty()) {
            YearMonth ym = YearMonth.parse(yearMonth);
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        } else if (year != null && month != null) {
            YearMonth ym = YearMonth.of(year, month);
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        } else {
            YearMonth ym = YearMonth.now();
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        }

        List<Absence> absences = absenceRepository.findAbsencesInMonth(start, end);

        List<AbsenceResponseDTO> response = absences.stream().map(a -> new AbsenceResponseDTO(
                a.getId(),
                a.getTitle(),
                a.getUser() != null ? a.getUser().getName() + " " + a.getUser().getSurname() : "Tots",
                a.getType().name(),
                a.getStartDate().toString(),
                a.getEndDate().toString(),
                a.getUser() != null ? a.getUser().getAvatarUrl() : null
        )).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/absence")
    public ResponseEntity<?> createAbsence(@RequestBody AbsenceRequestDTO request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Absence absence = new Absence();
        absence.setUser(user);
        absence.setTitle(request.getTitle() != null ? request.getTitle() : "Ausencia");

        try {
            absence.setType(AbsenceType.valueOf(request.getType().toUpperCase()));
        } catch (Exception e) {
            absence.setType(AbsenceType.HOLIDAY);
        }

        absence.setStartDate(LocalDate.parse(request.getStartDate()));
        absence.setEndDate(LocalDate.parse(request.getEndDate()));

        absenceRepository.save(absence);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/clock")
    public ResponseEntity<?> registerClock(@RequestBody ClockRequestDTO request, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        LocalDate today = LocalDate.now();
        
        Attendance attendance = attendanceRepository.findByUserAndDate(user, today)
                .orElseGet(() -> {
                    Attendance newAttendance = new Attendance();
                    newAttendance.setUser(user);
                    newAttendance.setDate(today);
                    return newAttendance;
                });

        LocalTime timeToSet = LocalTime.now();
        
        if (request.getTime() != null && !request.getTime().isEmpty()) {
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(r -> r.toString().contains("SUPERADMIN") || r.toString().contains("OWNER"));
            
            if (isAdmin) {
                timeToSet = LocalTime.parse(request.getTime());
            }
        }

        if ("IN".equalsIgnoreCase(request.getType())) {
            attendance.setCheckInTime(timeToSet);
        } else if ("OUT".equalsIgnoreCase(request.getType())) {
            attendance.setCheckOutTime(timeToSet);
        }

        attendanceRepository.save(attendance);
        return ResponseEntity.ok().build();
    }
}