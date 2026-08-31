package com.dynalar.dynalar.respository;

import com.dynalar.dynalar.model.user.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    Optional<TimeLog> findByUserIdAndDate(Long userId, LocalDate date);
    List<TimeLog> findByUserIdOrderByDateDesc(Long userId);
    List<TimeLog> findByDateBetween(LocalDate startDate, LocalDate endDate);
}