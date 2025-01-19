package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.util.Constants;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.util.Constants.*;

/**
 * This class provide the endpoints for Fixed Asset. <br/>
 * <b>Class</b>: {@link FinancialFixedAssetController}<br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/financial-fixed-assets")
public class FinancialFixedAssetController {

    @Autowired
    private FinancialFixedAssetService service;

    @Autowired
    private ReportJasperService reportJasperService;

    private ResponseEntity<ResponseData> handleException(String messageError, Exception e, HttpStatus statusCode) {
        ResponseData responseData = new ResponseData();
        ExceptionResponse exceptionResponse = new ExceptionResponse();

        responseData.setTimestamp(LocalDateTime.now());
        responseData.setCode(Constants.S_VAL_01);
        responseData.setMessage(messageError);
        exceptionResponse.setMessage(messageError);
        exceptionResponse.setDetails("Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause());
        exceptionResponse.setTimestamp(LocalDateTime.now());
        responseData.setException(exceptionResponse);

        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseData);
    }

    @Operation(summary = "Subir información del archivo Excel (Formato 7)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo subido correctamente.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "417", description = "Error al cargar archivo.", content = @Content)
    })
    @PostMapping("/upload")
    public ResponseEntity<ResponseData> uploadInputDataController(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam(name = "period") String period,
                                                                  HttpServletRequest request) {
        if (!hasExcelFormat(file)) {
            return buildResponseWithError("Formato no permitido. Debe ser .xlsx", HttpStatus.EXPECTATION_FAILED, file.getOriginalFilename());
        }

        try {
            List<InputData> listInputData = service.loadExcelFileToObjectService(file, period);
            request.getSession().setAttribute(Constants.LIST_INPUT_DATA, listInputData);
            return buildResponse("Archivo subido correctamente: " + file.getOriginalFilename(), listInputData.size(), HttpStatus.OK);
        } catch (Exception e) {
            return handleException("Error al subir archivo.", e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Guardar información subida al sistema en base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Información guardada correctamente.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "417", description = "Error al guardar la información.", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ResponseData> saveFinancialFixedAssetController(@RequestParam(name = "period") String period,
                                                                          HttpSession session) {
        String messageError = "Error al obtener información de entrada para el periodo " + period;
        List<InputData> listInputData = (List<InputData>) session.getAttribute(Constants.LIST_INPUT_DATA);
        if (listInputData == null || listInputData.isEmpty()) {
            return buildResponseWithError(messageError, HttpStatus.EXPECTATION_FAILED, period);
        }

        try {
            LocalDate periodDate = LocalDate.parse(period, DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD));
            service.deleteByAccountingPeriod(periodDate);
            service.saveAllService(period, listInputData);
            return buildResponse("Información guardada correctamente para el periodo: " + period, listInputData.size(), HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Generar proyección de deducciones temporales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proyección generada correctamente.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "417", description = "Error al generar proyección.", content = @Content)
    })
    @PostMapping("/projection")
    public ResponseEntity<ResponseData> generateProjectionController(@RequestParam(name = "period") String period,
                                                                     @RequestParam(name = "account") String account) {
        String messageError = "Error al generar proyección.";
        try {
            LocalDate periodDate = LocalDate.parse(period, DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD));
            List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodAndAccountStartingWithService(periodDate, account);

            if (listFinancialFixedAsset.isEmpty()) {
                return buildResponseWithError(messageError, HttpStatus.EXPECTATION_FAILED, period);
            }

            service.generateTemporayDeductionsService(periodDate, listFinancialFixedAsset);
            return buildResponse("Proyección generada correctamente.", listFinancialFixedAsset.size(), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Obtener información guardada en base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo informacion para el periodo.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "404", description = "Error al obtener informacion para el periodo.",
                    content = @Content)
    })
    @GetMapping("/{option}")
    public ResponseEntity<ResponseData> getFinancialFixedAssetController(@PathVariable int option,
                                                                         @RequestParam String period,
                                                                         @RequestParam String mainFixedAssetCode,
                                                                         @RequestParam String accountingAccount) {
        try {
            switch (option) {
                case N_VAL_1:
                    return handleFindByPeriod(period);
                case N_VAL_2:
                    return handleFindByMainFixedAssetCode(mainFixedAssetCode);
                case N_VAL_3:
                    return handleFindByAccountingAccount(accountingAccount);
                default:
                    return buildResponseWithError("Error al obtener informacion del activo fijo.", HttpStatus.EXPECTATION_FAILED, "");
            }
        } catch (Exception e) {
            String message = "Error al obtener informacion del activo fijo: " + accountingAccount;
            String detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            return buildResponseWithError(message, HttpStatus.EXPECTATION_FAILED, detailError);
        }
    }

    @Operation(summary = "Obtener reporte del Libro Tributario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se obtuvo informacion para el periodo.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseData.class))}),
            @ApiResponse(responseCode = "404", description = "Error al obtener informacion para el periodo.",
                    content = @Content)
    })
    @PostMapping(value = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> callGetReportTaxBookController(@RequestParam("fileType") String fileType,
                                                            @RequestParam String year,
                                                            @RequestParam String mainCode,
                                                            @RequestParam String accountingAccount,
                                                            @RequestParam String startAcquisitionDate,
                                                            @RequestParam String endAcquisitionDate,
                                                            @RequestParam String startDateUse,
                                                            @RequestParam String endDateUse) {
        try {
            ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
            ParamReportDto params = new ParamReportDto();
            params.setYear(Integer.parseInt(year));
            params.setMainCode(mainCode);
            params.setAccountingAccount(accountingAccount);
            params.setStartAcquisitionDate(startAcquisitionDate);
            params.setEndAcquisitionDate(endAcquisitionDate);
            params.setStartDateUse(startDateUse);
            params.setEndDateUse(endDateUse);
            List<FinancialFixedAssetDto> reportList = service.callGetReportTaxBookService(params);
            if (!reportList.isEmpty() && reportList != null) {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("companyName", "EPS GRAU S.A");
                parameters.put("period", year);
                parameters.put("ruc", "2010276292");
                byte[] data = reportJasperService.getJasperReportInBytes("rpt_tax_book.jrxml", fileType, parameters,
                        reportList);
                if (null != data) {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                    InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
                    String fileName = "Libro_Tributario_Activo_Fijo" + "_" + LocalDateTime.now() + reportType.getExtension();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(new MediaType("application", "force-download"));
                    headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
                    return  ResponseEntity.ok()
                            .headers(headers)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .body(resource);
                } else {
                    throw new ResourceNotFoundException("Error al obtener reporte");
                }
            } else {
                throw new ResourceNotFoundException("No hay información para el periodo.");
            }
        } catch (Exception e) {
            String message = "Error al obtener informacion para el año: " + year;
            String detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            System.out.println("Message: " + message + "\nDetalles: " + detailError);
            throw new ResourceNotFoundException("Error al obtener reporte");
        }
    }

    private ResponseEntity<ResponseData> handleFindByPeriod(String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD);
        LocalDate periodDate = LocalDate.parse(period, formatter);
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodService(periodDate);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el periodo: " + period, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el periodo: " + period, HttpStatus.NOT_FOUND, "");
        }
    }

    private ResponseEntity<ResponseData> handleFindByMainFixedAssetCode(String mainFixedAssetCode) {
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el código de activo fijo principal: " + mainFixedAssetCode, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el código de activo fijo principal: " + mainFixedAssetCode, HttpStatus.NOT_FOUND, "");
        }
    }

    private ResponseEntity<ResponseData> handleFindByAccountingAccount(String accountingAccount) {
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByFixedAssetAccountingAccountService(accountingAccount);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para la cuenta contable: " + accountingAccount, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para la cuenta contable: " + accountingAccount, HttpStatus.NOT_FOUND, "");
        }
    }

    private ResponseEntity<ResponseData> buildResponse(String message, Object obj, HttpStatus statusCode) {
        ResponseData responseData = new ResponseData();
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setMessage(message);
        responseData.setResponse(obj);
        responseData.setCode(Constants.S_VAL_00);
        return ResponseEntity.status(statusCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseData);
    }

    private ResponseEntity<ResponseData> buildResponseWithError(String message, HttpStatus status, String details) {
        ResponseData responseData = new ResponseData();
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setMessage(message);
        responseData.setCode(Constants.S_VAL_01);
        responseData.setException(new ExceptionResponse(LocalDateTime.now(), message, details));
        return new ResponseEntity<>(responseData, status);
    }

}
