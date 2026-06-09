package com.dynalar.dynalar.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dynalar.dynalar.model.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

}
