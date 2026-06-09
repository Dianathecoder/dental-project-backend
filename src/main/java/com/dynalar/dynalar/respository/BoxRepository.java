package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dynalar.dynalar.model.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {
	
}
