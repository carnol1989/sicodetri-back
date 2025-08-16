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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing SUNAT tax table entries.
 * Exposes endpoints to retrieve a specific tax rate by ID or accounting account,
 * and to fetch all tax table entries.
 * <b>Class</b>: {@link TaxTableSunatController}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
 *     <li>Jun 06, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/tax-tables")
public class TaxTableSunatController {

    @Autowired
    private TaxTableSunatService service;

    /**
     * Retrieves a SUNAT tax rate entry based on the specified search option.
     *
     * @param option            the search option (1 = by ID, 2 = by accounting account)
     * @param id                the tax table record ID when option = 1
     * @param accountingAccount the accounting account when option = 2
     * @return a ResponseEntity containing ResponseData with the found TaxTableSunat,
     *         or an error payload if not found or on failure
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
    @PreAuthorize("hasRole('TaxTableViewer')")
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
     * Builds a standardized error response without an exception detail.
     *
     * @param responseData      the response data object to populate
     * @param exceptionResponse the exception response wrapper
     * @param message           the error message
     * @param statusCode        the HTTP status to return
     * @return a ResponseEntity containing ResponseData with error detail
     */
    private ResponseEntity<ResponseData> buildErrorResponse(ResponseData responseData,
        ExceptionResponse exceptionResponse, String message, HttpStatus statusCode) {
        return buildErrorResponse(responseData, exceptionResponse, message, statusCode, null);
    }

    /**
     * Builds a standardized error response including exception details.
     *
     * @param responseData      the response data object to populate
     * @param exceptionResponse the exception response wrapper
     * @param message           the error message
     * @param statusCode        the HTTP status to return
     * @param e                 the exception that occurred (may be null)
     * @return a ResponseEntity containing ResponseData with error and exception details
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
     * Retrieves all entries in the SUNAT tax table.
     *
     * @return a ResponseEntity containing a list of TaxTableSunat objects
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaxTableSunat>> getAllTaxesController() {
        List<TaxTableSunat> taxes = service.findAllService();
        return ResponseEntity.ok(taxes);
    }

}