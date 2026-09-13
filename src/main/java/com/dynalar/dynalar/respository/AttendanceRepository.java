package com.dynalar.dynalar.respository;

import com.dynalar.dynalar.model.staffClock.Attendance;
import com.dynalar.dynalar.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
}