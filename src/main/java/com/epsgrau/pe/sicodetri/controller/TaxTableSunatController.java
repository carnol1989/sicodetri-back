package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_1;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_2;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Obtain SUNAT tax table information.
     *
     * @param option Option of search
     * @param id Identifier
     * @param accountingAccount Accounting account
     * @return ResponseEntity of ResponseData
     */
    @Operation(summary = "Obtener información de tabla de impuesto de la SUNAT.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Se obtuvo informacion con ID proporcionado.",
                content = {
                    @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(
                responseCode = "404",
                description = "ID no encontrado.",
                content = @Content)})
    @GetMapping("/{option}")
    public ResponseEntity<ResponseData> getTaxTableSunatController(@PathVariable Long option,
        @RequestParam Long id, @RequestParam String accountingAccount) {
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();

        responseData.setTimestamp(LocalDateTime.now());

        try {
            TaxTableSunat obj = null;
            String message;

            switch (option.intValue()) {
              case N_VAL_1:
                  obj = service.findByIdService(id);
                  message = "Se obtuvo información para ID: " + id;
                  break;
              case N_VAL_2:
                  obj = service.findByAccountigAccountService(accountingAccount);
                  message = "Se obtuvo información para cuenta contable: " + accountingAccount;
                  break;
              default:
                  return buildErrorResponse(responseData, exceptionResponse, "Error al "
                      + "obtener informacion de la tasa Sunat.", HttpStatus.EXPECTATION_FAILED);
            }

            // Si no se encuentra el objeto
            if (obj == null || obj.getId() == null) {
                return buildErrorResponse(responseData, exceptionResponse, "Error al obtener "
                    + "informacion con el ID o cuenta contable proporcionado.",
                    HttpStatus.NOT_FOUND);
            }

            responseData.setResponse(obj);
            responseData.setMessage(message);
            HttpStatus statusCode = HttpStatus.OK;
            return ResponseEntity.status(statusCode)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseData);
        } catch (Exception e) {
            return buildErrorResponse(responseData, exceptionResponse, "Error al obtener "
                + "informacion del activo fijo: " + accountingAccount,
                HttpStatus.EXPECTATION_FAILED, e);
        }
    }

    /**
     * Build error response.
     *
     * @param responseData Response data
     * @param exceptionResponse Object exception responde
     * @param message Message
     * @param statusCode HttpStatusCode
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> buildErrorResponse(ResponseData responseData,
        ExceptionResponse exceptionResponse, String message, HttpStatus statusCode) {
        return buildErrorResponse(responseData, exceptionResponse, message, statusCode, null);
    }

    /**
     * Build error response.
     *
     * @param responseData ResponseData
     * @param exceptionResponse ExceptionResponse
     * @param message Message
     * @param statusCode HttpStatusCode
     * @param e Object exception
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> buildErrorResponse(ResponseData responseData,
        ExceptionResponse exceptionResponse, String message, HttpStatus statusCode, Exception e) {

        responseData.setCode(Constants.S_VAL_01);
        responseData.setMessage(message);
        responseData.setTimestamp(LocalDateTime.now());

        exceptionResponse.setMessage(message);
        String detailError = e != null ? "Mensaje: " + e.getMessage() + "\nCausa: "
            + e.getCause() : "";
        exceptionResponse.setDetails(detailError);
        exceptionResponse.setTimestamp(LocalDateTime.now());

        responseData.setException(exceptionResponse);

        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseData);
    }

    /**
     * Obtain information on the entire SUNAT tax table.
     *
     * @return ResponseEntity of List of TaxTableSunat
     */
    @Operation(summary = "Obtener información de toda la tabla de impuesto de la SUNAT.")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Se obtuvo informacion.",
                content = {
                    @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(
                responseCode = "404",
                description = "No se encontró información.",
                content = @Content)})
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaxTableSunat>> getAllTaxesController() {
        List<TaxTableSunat> taxes = service.findAllService();
        return ResponseEntity.ok(taxes);
    }

}
