package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.Treatment;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

}