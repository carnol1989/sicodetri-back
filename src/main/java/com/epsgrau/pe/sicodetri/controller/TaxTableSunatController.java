package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tax-tables")
public class TaxTableSunatController {

    @Autowired
    private TaxTableSunatService service;

    @GetMapping("/{id}")
    public ResponseEntity<TaxTableSunat> getTaxTableByIdController(@PathVariable Long id) {
        TaxTableSunat obj = service.findByIdService(id);
        if (obj.getId() == null) {
            throw new ResourceNotFoundException("ID no encontrado " + id);
        }
        return new ResponseEntity<TaxTableSunat>(obj, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<TaxTableSunat>> getAllTaxesController() {
        List<TaxTableSunat> taxes = service.findAllService();
        return ResponseEntity.ok(taxes);
    }

}
