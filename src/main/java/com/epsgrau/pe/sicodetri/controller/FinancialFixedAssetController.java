package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.util.Constants.COMPANY_NAME;
import static com.epsgrau.pe.sicodetri.util.Constants.COMPANY_NAME_VALUE;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_1;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_3;
import static com.epsgrau.pe.sicodetri.util.Constants.PERIOD;
import static com.epsgrau.pe.sicodetri.util.Constants.RUC;
import static com.epsgrau.pe.sicodetri.util.Constants.RUC_VALUE;

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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@Tag(name = "Financial Fixed Asset", description = "Gestión de activos fijos "
    + "financieros y sus proyecciones")
@Slf4j
@RestController
@RequestMapping("/api/v1/financial-fixed-assets")
public class FinancialFixedAssetController {

    @Autowired
    private FinancialFixedAssetService service;

    @Autowired
    private ReportJasperService reportJasperService;

    /**
     * Handles internal exceptions to build a ResponseEntity with error code.
     *
     * @param messageError Message of error
     * @param e            Object Exception
     * @param statusCode   HttpStatus Code
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> handleException(String messageError, Exception e,
                                                         HttpStatus statusCode) {
        ResponseData responseData = new ResponseData();
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setCode(Constants.S_VAL_01);
        responseData.setMessage(messageError);
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setMessage(messageError);
        exceptionResponse.setDetails("Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause());
        exceptionResponse.setTimestamp(LocalDateTime.now());
        responseData.setException(exceptionResponse);
        return ResponseEntity.status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseData);
    }

    /**
     * Upload excel file with data assets.
     *
     * @param file Excel file input
     * @param period Accounting period
     * @param request Object request
     * @return ResponseEntity of ResponseData
     */
    @Operation(summary = "Cargar archivo Excel con datos de activos fijos (Formato 7)")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Archivo subido correctamente.",
                content = {
                    @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class))
                }),
            @ApiResponse(
                responseCode = "417",
                description = "Error al cargar archivo.",
                content = @Content)
        }
    )
    @PostMapping(value = "/upload", produces = "application/json")
    public ResponseEntity<ResponseData> uploadInputDataController(
        @Parameter(description = "Archivo Excel de entrada")
        @RequestParam("file") MultipartFile file,
        @Parameter(description = "Periodo contable", example = "2022-01-01")
        @RequestParam(name = "period") String period,
        HttpServletRequest request) {
        if (!hasExcelFormat(file)) {
            return buildResponseWithError("Formato no permitido. Debe ser .xlsx",
                HttpStatus.EXPECTATION_FAILED, file.getOriginalFilename());
        }

        try {
            List<InputData> listInputData = service.loadExcelFileToObjectService(file, period);
            log.info("Cantidad registros leidos: {}", listInputData.size());
            request.getSession().setAttribute(Constants.LIST_INPUT_DATA, listInputData);
            return buildResponse("Archivo subido correctamente: " + file.getOriginalFilename(),
                listInputData.size(), HttpStatus.OK);
        } catch (Exception e) {
            return handleException("Error al subir archivo.", e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Save asset from Excel file.
     *
     * @param period Accounting period
     * @param session Object session
     * @return ResponseEntity of ResponseData
     */
    @Operation(summary = "Guardar activos fijos desde la sesión")
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Información guardada correctamente.",
                content = {
                    @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ResponseData.class))
                }),
            @ApiResponse(
                responseCode = "417",
                description = "Error al guardar la información.", content = @Content)
        }
    )
    @PostMapping
    public ResponseEntity<ResponseData> saveFinancialFixedAssetController(
        @Parameter(description = "Periodo contable", example = "2022-01-01")
        @RequestParam(name = "period") String period,
        HttpSession session) {
        String messageError = "Error al obtener información de entrada "
            + "para el periodo " + period;
        List<InputData> listInputData = (List<InputData>) session
            .getAttribute(Constants.LIST_INPUT_DATA);
        if (listInputData == null || listInputData.isEmpty()) {
            return buildResponseWithError(messageError, HttpStatus.EXPECTATION_FAILED, period);
        }
        log.info("Cantidad registros a guardar: {}", listInputData.size());
        try {
            LocalDate periodDate = LocalDate.parse(period, DateTimeFormatter
                .ofPattern(Constants.FORMATTER_YYYY_MM_DD));
            service.deleteByAccountingPeriod(periodDate);
            service.saveAllService(period, listInputData);
            return buildResponse("Información guardada correctamente para el periodo: " + period,
                listInputData.size(), HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Generate projection of temporary deductions.
     *
     * @param period Accounting period
     * @param account Accounting account
     * @return ResponseEntity of ResponseData
     */
    @Operation(summary = "Generar proyección de deducciones temporales")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Proyección generada correctamente.",
            content = {
                @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))}),
        @ApiResponse(
            responseCode = "417",
            description = "Error al generar proyección.",
            content = @Content)
    })
    @PostMapping("/projection")
    public ResponseEntity<ResponseData> generateProjectionController(
        @Parameter(description = "Periodo contable", example = "2022-01-01")
        @RequestParam(name = "period") String period,
        @Parameter(description = "Cuenta contable que inicia con", example = "332")
        @RequestParam(name = "account") String account) {
        String messageError = "Error al generar proyección.";
        try {
            LocalDate periodDate = LocalDate.parse(period,
                DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD));

            List<FinancialFixedAsset> listFinancialFixedAsset =
                service.findByPeriodAndAccountStartingWithService(periodDate, account);
            log.info("listFinancialFixedAsset: {}", listFinancialFixedAsset.size());

            if (listFinancialFixedAsset.isEmpty()) {
                return buildResponseWithError(messageError, HttpStatus.EXPECTATION_FAILED, period);
            }

            service.generateTemporayDeductionsService(periodDate, listFinancialFixedAsset);
            return buildResponse("Proyección generada correctamente.",
                listFinancialFixedAsset.size(), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get information of assets.
     *
     * @param option Option search
     * @param period Accounting period
     * @param mainFixedAssetCode Main fixed asset code
     * @param accountingAccount Accounting account
     * @return ResponseEntity of ResponseData
     */
    @Operation(summary = "Obtener información de activos fijos.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Se obtuvo informacion para el periodo.",
            content = {
                @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))}),
        @ApiResponse(
            responseCode = "404",
            description = "Error al obtener informacion para el periodo.",
            content = @Content)
    })
    @GetMapping("/{option}")
    public ResponseEntity<ResponseData> getFinancialFixedAssetController(
        @Parameter(description = "Opción de búsqueda (1 = periodo, 2 = código activo fijo, "
            + "3 = cuenta contable)") @PathVariable int option,
        @Parameter(description = "Periodo contable", example = "2022-01-01")
        @RequestParam String period,
        @Parameter(description = "Código activo fijo principal", example = "22.01.010862")
        @RequestParam String mainFixedAssetCode,
        @Parameter(description = "Cuenta contable", example = "3321161001")
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
                  return buildResponseWithError("Error al obtener informacion "
                      + "del activo fijo.", HttpStatus.EXPECTATION_FAILED, "");
            }
        } catch (Exception e) {
            String message = "Error al obtener informacion del activo fijo: " + accountingAccount;
            String detailError = "Mensaje: " + e.getMessage() + "\nCausa: " + e.getCause();
            return buildResponseWithError(message, HttpStatus.EXPECTATION_FAILED, detailError);
        }
    }

    /**
     * Get report Tax Book Fixed.
     *
     * @param option Option report
     * @param fileType File type
     * @param year Accounting year
     * @param mainCode Main code
     * @param accountingAccount Accounting account
     * @param startAcquisitionDate Start acquisition date
     * @param endAcquisitionDate End acquisition date
     * @param startDateUse Start date use
     * @param endDateUse End date use
     * @return ResponseEntity of report
     */
    @Operation(summary = "Obtener reporte del Libro Tributario.")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Se obtuvo informacion para el periodo.",
            content = {
                @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))}),
        @ApiResponse(
            responseCode = "404",
            description = "Error al obtener informacion para el periodo.",
            content = @Content)
    })
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> callGetReportTaxBookController(
        @Parameter(description = "Opción de búsqueda (1 = periodo, 2 = código activo fijo, "
            + "3 = cuenta contable)") @RequestParam("option") String option,
        @Parameter(description = "Tipo archivo a generar", example = "xlsx")
        @RequestParam("fileType") String fileType,
        @Parameter(description = "Año del periodo contable", example = "2022")
        @RequestParam String year,
        @Parameter(description = "Código activo fijo principal", example = "22.01.010862")
        @RequestParam String mainCode,
        @Parameter(description = "Código contable del activo fijo", example = "3321161001")
        @RequestParam String accountingAccount,
        @Parameter(description = "Fecha de inicio de adquisición del Activo Fijo",
            example = "2007-12-01") @RequestParam String startAcquisitionDate,
        @Parameter(description = "Fecha de fin de adquisición del Activo Fijo",
            example = "2007-12-31") @RequestParam String endAcquisitionDate,
        @Parameter(description = "Fecha de incio de uso del Activo Fijo", example = "2007-12-01")
        @RequestParam String startDateUse,
        @Parameter(description = "Fecha de fin de uso del Activo Fijo", example = "2007-12-31")
        @RequestParam String endDateUse) {
        try {
            // 1. Construir parámetros
            ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
            ParamReportDto params = new ParamReportDto();
            params.setYear(Integer.parseInt(year));
            params.setMainCode(mainCode);
            params.setAccountingAccount(accountingAccount);
            params.setStartAcquisitionDate(startAcquisitionDate);
            params.setEndAcquisitionDate(endAcquisitionDate);
            params.setStartDateUse(startDateUse);
            params.setEndDateUse(endDateUse);

            // 2. Llamar al servicio para obtener lista de DTOs
            List<FinancialFixedAssetDto> reportList = service.callGetReportTaxBookService(params);

            // 3. Si la lista está vacía, lanzar excepción que NO se captura aquí
            if (reportList.isEmpty()) {
                throw new ResourceNotFoundException("No hay información para el periodo.");
            }

            log.info("reportList: {}", reportList.size());

            // 4. Generar el reporte con Jasper y devolver binario
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(COMPANY_NAME, COMPANY_NAME_VALUE);
            parameters.put(PERIOD, year);
            parameters.put(RUC, RUC_VALUE);
            byte[] data = reportJasperService.getJasperReportInBytes("rpt_tax_book.jrxml",
                fileType, parameters, reportList);
            if (data != null) {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                InputStreamResource resource = new InputStreamResource(byteArrayInputStream);
                String fileName = "Libro_Tributario_Activo_Fijo" + "_"
                    + LocalDateTime.now() + reportType.getExtension();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "force-download"));
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
                return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
            } else {
                throw new ResourceNotFoundException("Error al obtener reporte");
            }
        } catch (ResourceNotFoundException rnfe) {
            throw rnfe;
        } catch (SQLException e) {
            log.error("Error SQLException: {}\nCausa: {}\nError Code: {}"
                    + "\nSQL State: {}", e.getMessage(), e.getCause(),
                e.getErrorCode(), e.getSQLState());
            throw new RuntimeException("Error al obtener reporte");
        } catch (JRException e) {
            log.error("Error JRException: {}\nCausa: {}", e.getMessage(), e.getCause());
            throw new RuntimeException("Error al obtener reporte");
        } catch (FileNotFoundException e) {
            log.error("Error SQLException: {}\nCausa: {}", e.getMessage(), e.getCause());
            throw new RuntimeException("Error al obtener reporte");
        }
    }

    /**
     * Handle find by period.
     *
     * @param period Accounting period
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> handleFindByPeriod(String period) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD);
        LocalDate periodDate = LocalDate.parse(period, formatter);
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodService(periodDate);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el periodo: "
                + period, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el periodo: "
                + period, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Handle find by main fixed asset code.
     *
     * @param mainFixedAssetCode Main fixed asset code
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> handleFindByMainFixedAssetCode(String mainFixedAssetCode) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByMainFixedAssetCodeService(mainFixedAssetCode);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el código de activo fijo principal: "
                + mainFixedAssetCode, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el código de "
                + "activo fijo principal: " + mainFixedAssetCode, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Handle find by accountign account.
     *
     * @param accountingAccount Accounting account
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> handleFindByAccountingAccount(String accountingAccount) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByFixedAssetAccountingAccountService(accountingAccount);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para la cuenta contable: "
                + accountingAccount, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para la cuenta contable: "
                + accountingAccount, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Build response.
     *
     * @param message Message
     * @param obj Object response
     * @param statusCode HttpStatusCode
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> buildResponse(String message,
                                                       Object obj,
                                                       HttpStatus statusCode) {
        ResponseData responseData = new ResponseData();
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setMessage(message);
        responseData.setResponse(obj);
        responseData.setCode(Constants.S_VAL_00);
        return ResponseEntity.status(statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(responseData);
    }

    /**
     * Build response with error.
     *
     * @param message Message
     * @param status HttpStatusCode
     * @param details Details error
     * @return ResponseEntity of ResponseData
     */
    private ResponseEntity<ResponseData> buildResponseWithError(String message,
                                                                HttpStatus status,
                                                                String details) {
        ResponseData responseData = new ResponseData();
        responseData.setTimestamp(LocalDateTime.now());
        responseData.setMessage(message);
        responseData.setCode(Constants.S_VAL_01);
        responseData.setException(new ExceptionResponse(LocalDateTime.now(), message, details));
        return new ResponseEntity<>(responseData, status);
    }
}
