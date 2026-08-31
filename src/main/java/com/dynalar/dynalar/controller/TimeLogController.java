package com.dynalar.dynalar.controller;

import com.dynalar.dynalar.model.user.TimeLog;
import com.dynalar.dynalar.model.user.User;
import com.dynalar.dynalar.respository.TimeLogRepository;
import com.dynalar.dynalar.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/time-log")
public class TimeLogController {

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private UserRepository userRepository;

    // Fichar Entrada
    @PostMapping("/clock-in")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clockIn(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        LocalDate today = LocalDate.now();

        Optional<TimeLog> existingLog = timeLogRepository.findByUserIdAndDate(user.getId(), today);
        if (existingLog.isPresent() && existingLog.get().getClockIn() != null) {
            return ResponseEntity.badRequest().body("Ya has fichado la entrada hoy.");
        }

        TimeLog log = existingLog.orElse(new TimeLog());
        log.setUser(user);
        log.setDate(today);
        log.setClockIn(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CREATED).body(timeLogRepository.save(log));
    }

    // Fichar Salida
    @PostMapping("/clock-out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> clockOut(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userOpt.get();
        LocalDate today = LocalDate.now();

        Optional<TimeLog> existingLog = timeLogRepository.findByUserIdAndDate(user.getId(), today);
        if (existingLog.isEmpty() || existingLog.get().getClockIn() == null) {
            return ResponseEntity.badRequest().body("No has fichado la entrada hoy todavía.");
        }

        TimeLog log = existingLog.get();
        if (log.getClockOut() != null) {
            return ResponseEntity.badRequest().body("Ya has fichado la salida hoy.");
        }

        log.setClockOut(LocalDateTime.now());
        return ResponseEntity.ok(timeLogRepository.save(log));
    }

    // Ver mis propios fichajes
    @GetMapping("/my-logs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TimeLog>> getMyLogs(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(timeLogRepository.findByUserIdOrderByDateDesc(userOpt.get().getId()));
    }

  
 // Ver todos los fichajes (Exclusivo ADMIN)
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TimeLog>> getAllLogs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        if (start != null && end != null) {
            return ResponseEntity.ok(timeLogRepository.findByDateBetween(start, end));
        }

        return ResponseEntity.ok(timeLogRepository.findAll());
    }

    // Ajuste manual de fichajes (EXCLUSIVO ADMIN)
    @PutMapping("/{logId}/adjust")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> adjustTimeLog(
            @PathVariable Long logId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newClockIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newClockOut,
            @RequestParam String reason) {

        Optional<TimeLog> logOpt = timeLogRepository.findById(logId);
        if (logOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TimeLog log = logOpt.get();

        if (newClockIn != null) {
            log.setClockIn(newClockIn);
        }
        if (newClockOut != null) {
            log.setClockOut(newClockOut);
        }

        log.setModifiedByAdmin(true);
        log.setAdminNotes(reason);

        return ResponseEntity.ok(timeLogRepository.save(log));
    }
}