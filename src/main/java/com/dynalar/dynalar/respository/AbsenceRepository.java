package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dynalar.dynalar.model.staffClock.Absence;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    
    @Query("SELECT a FROM Absence a WHERE a.startDate <= :endOfMonth AND a.endDate >= :startOfMonth")
    List<Absence> findAbsencesInMonth(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Absence a WHERE a.user.id = :staffId AND :targetDate BETWEEN a.startDate AND a.endDate")
    boolean isStaffAbsentOnDate(@Param("staffId") Long staffId, @Param("targetDate") LocalDate targetDate);
}