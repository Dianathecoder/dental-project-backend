package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dynalar.dynalar.model.staffClock.Absence;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    @Query("SELECT a FROM Absence a WHERE a.startDate <= :endOfMonth AND a.endDate >= :startOfMonth")
    List<Absence> findAbsencesInMonth(LocalDate startOfMonth, LocalDate endOfMonth);
}
