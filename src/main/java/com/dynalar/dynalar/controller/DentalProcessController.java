package com.dynalar.dynalar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dynalar.dynalar.model.odontogram.DentalProcess;
import com.dynalar.dynalar.respository.DentalProcessRepository;

@RestController
@RequestMapping("/process")
public class DentalProcessController {

	@Autowired
    private DentalProcessRepository dentalProcessRepository;

	@RequestMapping("/index")
    public ResponseEntity<List<DentalProcess>> getAllDentalProcesses() {
        try {
            return ResponseEntity.ok(dentalProcessRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
