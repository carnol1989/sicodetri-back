package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This class provide the endpoints for Tax Table of SUNAT. <br/>
 * <b>Class</b>: {@link TaxTableSunatController}<br/>
 * <b>Copyright</b>: &Copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau <br/>
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>: <br/>
 *     <ul>
 *     <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:<br/>
 *     <ul>
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/tax-tables")
public class TaxTableSunatController {

    @Autowired
    private TaxTableSunatService service;

    @Operation(summary = "Obtener información por ID de tabla de impuesto de la SUNAT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo informacion con ID proporcionado.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "404", description = "ID no encontrado.",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaxTableSunat> getTaxTableByIdController(@PathVariable Long id) {
        TaxTableSunat obj = service.findByIdService(id);
        if (obj.getId() == null) {
            throw new ResourceNotFoundException("ID no encontrado " + id);
        }
        return new ResponseEntity<TaxTableSunat>(obj, HttpStatus.OK);
    }

    @Operation(summary = "Obtener información de toda la tabla de impuesto de la SUNAT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo informacion.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "404", description = "No se encontró información.",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TaxTableSunat>> getAllTaxesController() {
        List<TaxTableSunat> taxes = service.findAllService();
        return ResponseEntity.ok(taxes);
    }

}
