package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_1;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_2;

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

    @Operation(summary = "Obtener información de tabla de impuesto de la SUNAT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo informacion con ID proporcionado.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "404", description = "ID no encontrado.",
                    content = @Content)
    })
    @GetMapping("/{option}")
    public ResponseEntity<ResponseData> getTaxTableSunatController(@PathVariable Long option,
                                                                   @RequestParam Long id,
                                                                   @RequestParam String accountingAccount) {
        String code = "00";
        String message = "";
        String detailError = "";
        HttpStatus statusCode = HttpStatus.OK;
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        try {
            responseData.setTimestamp(LocalDateTime.now());
            TaxTableSunat obj = new TaxTableSunat();
            if (option == N_VAL_1) {
                responseData.setTimestamp(LocalDateTime.now());
                obj = service.findByIdService(id);
                message = "Se obtuvo información para ID: " + id;
                responseData.setResponse(obj);
                if (obj.getId() == null) {
                    code = "01";
                    statusCode = HttpStatus.NOT_FOUND;
                    responseData.setTimestamp(LocalDateTime.now());
                    message = "Error al obtener informacion con el ID: " + id;
                    exceptionResponse.setMessage(message);
                    exceptionResponse.setTimestamp(LocalDateTime.now());
                    exceptionResponse.setDetails(detailError);
                }
            } else if (option == N_VAL_2) {
                responseData.setTimestamp(LocalDateTime.now());
                obj = service.findByAccountigAccountService(accountingAccount);
                message = "Se obtuvo informacion para cuenta contable: " + accountingAccount;
                responseData.setResponse(obj);
                if (obj.getId() == null) {
                    code = "01";
                    statusCode = HttpStatus.NOT_FOUND;
                    responseData.setTimestamp(LocalDateTime.now());
                    message = "Error al obtener informacion para cuenta contable: " + accountingAccount;
                    exceptionResponse.setMessage(message);
                    exceptionResponse.setTimestamp(LocalDateTime.now());
                    exceptionResponse.setDetails(detailError);
                }
            } else {
                code = "01";
                statusCode = HttpStatus.EXPECTATION_FAILED;
                responseData.setTimestamp(LocalDateTime.now());
                message = "Error al obtener informacion de la tasa Sunat.";
                exceptionResponse.setMessage(message);
                exceptionResponse.setTimestamp(LocalDateTime.now());
                exceptionResponse.setDetails(detailError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = "01";
            statusCode = HttpStatus.EXPECTATION_FAILED;
            responseData.setTimestamp(LocalDateTime.now());
            message = "Error al obtener informacion del activo fijo: " + accountingAccount;
            detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            exceptionResponse.setMessage(message);
            exceptionResponse.setTimestamp(LocalDateTime.now());
            exceptionResponse.setDetails(detailError);
        }
        responseData.setCode(code);
        responseData.setMessage(message);
        responseData.setException(exceptionResponse);
        return new ResponseEntity<ResponseData>(responseData, statusCode);
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
