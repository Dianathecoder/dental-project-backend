package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.dynalar.dynalar.model.user.Dentist;
import java.util.List;
import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long> {
    List<Dentist> findByTreatments_Id(Long treatmentId);
    Optional<Dentist> findByUserId(Long userId);
}