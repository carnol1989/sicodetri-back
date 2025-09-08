package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.util.Constants.ALL;
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
import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategyFactory;
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
import java.time.format.DateTimeParseException;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for managing financial fixed assets and their projections.
 * Exposes endpoints to upload Excel data, save fixed assets, generate projections,
 * retrieve asset information, and produce various reports.
 * <b>Class</b>: {@link FinancialFixedAssetController}<br/>
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

    @Autowired
    private ReportStrategyFactory reportStrategyFactory;

    /**
     * Handles internal exceptions and builds a standardized JSON error response.
     *
     * @param messageError the error message to return
     * @param e            the exception that was thrown
     * @param statusCode   the HTTP status to use for the response
     * @return a ResponseEntity containing a ResponseData with error details
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
     * Uploads an Excel file containing fixed asset data (Format 7) and stores it in the session.
     *
     * @param file    the uploaded Excel file
     * @param period  the accounting period in 'YYYY-MM-DD' format
     * @param request the HTTP servlet request, used to access the session
     * @return a ResponseEntity with a ResponseData indicating success or failure
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
        HttpServletRequest request,
        Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "ANONYMOUS";
        log.info("Usuario '{}' solicitó guardar activos para el periodo {}", username, period);

        if (!hasExcelFormat(file)) {
            return buildResponseWithError("Formato no permitido. Debe ser .xlsx",
                HttpStatus.EXPECTATION_FAILED, file.getOriginalFilename());
        }

        try {
            List<InputData> listInputData = service.loadExcelFileToObjectService(file, period,
                username);
            log.info("Cantidad registros leidos: {}", listInputData.size());
            request.getSession().setAttribute(Constants.LIST_INPUT_DATA, listInputData);
            return buildResponse("Archivo subido correctamente: " + file.getOriginalFilename(),
                listInputData.size(), HttpStatus.OK);
        } catch (Exception e) {
            return handleException("Error al subir archivo.", e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Saves fixed asset data previously uploaded and stored in the session.
     *
     * @param period the accounting period in 'YYYY-MM-DD' format
     * @param session the HTTP session containing uploaded data
     * @return a ResponseEntity with a ResponseData indicating success or failure
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
        HttpSession session,
        Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "ANONYMOUS";
        String messageError = "Error al obtener información de entrada "
            + "para el periodo " + period;
        List<InputData> listInputData = (List<InputData>) session
            .getAttribute(Constants.LIST_INPUT_DATA);
        if (listInputData == null || listInputData.isEmpty()) {
            return buildResponseWithError(messageError, HttpStatus.BAD_REQUEST, period);
        }
        log.info("Cantidad registros a guardar: {}", listInputData.size());
        try {
            LocalDate periodDate = LocalDate.parse(period, DateTimeFormatter
                .ofPattern(Constants.FORMATTER_YYYY_MM_DD));
            List<FinancialFixedAsset> listFinancialFixedAsset =
                service.findByPeriodAndAddedByService(periodDate, username);
            service.deleteAllFinancialFixedAsset(listFinancialFixedAsset);
            service.saveAllService(period, listInputData, username);
            return buildResponse("Información guardada correctamente para el periodo: " + period,
                listInputData.size(), HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Generates a projection for fixed assets (additions or deductions) based on type.
     *
     * @param type    the projection type (e.g., TEMPORARY_DEDUCTION, TEMPORARY_ADDITION)
     * @param period  the accounting period in 'YYYY-MM-DD' format
     * @param account the accounting account prefix or 'ALL' to process all
     * @return a ResponseEntity with a ResponseData indicating success or failure
     */
    @Operation(summary = "Generar proyección de Activos Fijos")
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
        @Parameter(description = "Tipo de proyección (e.g. TEMPORARY_DEDUCTION, "
            + "TEMPORARY_ADDITION)")
        @RequestParam(name = "type") ProjectionType type,
        @Parameter(description = "Periodo contable", example = "2022-01-01")
        @RequestParam(name = "period") String period,
        @Parameter(description = "Cuenta contable que inicia con", example = "332")
        @RequestParam(name = "account") String account,
        Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "ANONYMOUS";
        String messageError = "Error al generar proyección.";
        try {
            LocalDate periodDate = LocalDate.parse(period,
                DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD));

            List<FinancialFixedAsset> listFinancialFixedAsset;
            if (account.equalsIgnoreCase(ALL)) {
                List<String> prefixes = List.of("332", "333", "334", "335", "336", "337");
                listFinancialFixedAsset = service.findByAddedByAndAccountingAccountPrefixesService(username, prefixes);
            } else {
                listFinancialFixedAsset = service.findByPeriodAndAddedByAndAccountStartingWithService(
                    periodDate, account, username);
            }
            log.info("Activos a procesar ({}): {}", type, listFinancialFixedAsset.size());

            if (listFinancialFixedAsset.isEmpty()) {
                return buildResponseWithError(
                    messageError + " No se encontraron registros.",
                    HttpStatus.NOT_FOUND,
                    period);
            }

            service.generateProjectionService(type, periodDate, listFinancialFixedAsset, account,
                username);
            return buildResponse("Proyección generada correctamente.",
                listFinancialFixedAsset.size(), HttpStatus.CREATED);
        } catch (DateTimeParseException dtpe) {
            return buildResponseWithError(
                "Formato de fecha inválido. Use 'YYYY-MM-DD'.",
                HttpStatus.BAD_REQUEST,
                period
            );
        } catch (IllegalArgumentException iae) {
            return buildResponseWithError(
                "Tipo de proyección no soportado: " + iae.getMessage(),
                HttpStatus.BAD_REQUEST,
                period
            );
        } catch (Exception e) {
            return handleException(messageError, e, HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Retrieves financial fixed asset information based on the given option.
     *
     * @param option               the search option (1=period, 2=asset code, 3=account)
     * @param period               the accounting period in 'YYYY-MM-DD' format
     * @param mainFixedAssetCode   the main asset code (if option=2)
     * @param accountingAccount    the accounting account (if option=3)
     * @return a ResponseEntity with a ResponseData containing the assets or an error
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
        @RequestParam String accountingAccount,
        Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "ANONYMOUS";
        try {
            switch (option) {
              case N_VAL_1:
                  return handleFindByPeriod(period, username);
              case N_VAL_2:
                  return handleFindByMainFixedAssetCode(mainFixedAssetCode, username);
              case N_VAL_3:
                  return handleFindByAccountingAccount(accountingAccount, username);
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
     * Produces a report (e.g., tax book) as a file download.
     *
     * @param option                the report option
     * @param fileType              the file type/extension (e.g., "xlsx")
     * @param year                  the accounting year
     * @param mainCode              the main asset code
     * @param accountingAccount     the accounting account code
     * @param startAcquisitionDate  the acquisition start date (YYYY-MM-DD)
     * @param endAcquisitionDate    the acquisition end date (YYYY-MM-DD)
     * @param startDateUse          the usage start date (YYYY-MM-DD)
     * @param endDateUse            the usage end date (YYYY-MM-DD)
     * @return a ResponseEntity containing the report file stream or an error
     * @throws JRException             if Jasper report processing fails
     * @throws FileNotFoundException   if the report template is not found
     * @throws SQLException            if a database access error occurs
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
    public ResponseEntity<?> callGetReportsController(
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
        @RequestParam String endDateUse) throws JRException, FileNotFoundException, SQLException {
        try {
            ParamReportDto params = new ParamReportDto(Integer.parseInt(year), mainCode,
                accountingAccount, startAcquisitionDate, endAcquisitionDate, startDateUse,
                endDateUse);

            ReportStrategy strategy;
            try {
                strategy = reportStrategyFactory.getStrategy(option);
            } catch (ResourceNotFoundException rnfe) {
                return buildResponseWithError(
                    "Tipo de reporte no soportado para opción: " + option,
                    HttpStatus.NOT_FOUND,
                    rnfe.getMessage()
                );
            }

            List<FinancialFixedAssetDto> reportList = strategy.generateReport(params);
            log.debug("reportList: {}", reportList.size());

            if (reportList.isEmpty()) {
                return buildResponseWithError(
                    "No hay información para el periodo.",
                    HttpStatus.NOT_FOUND,
                    "Periodo: " + year + ", Opción: " + option);
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put(COMPANY_NAME, COMPANY_NAME_VALUE);
            parameters.put(PERIOD, year);
            parameters.put(RUC, RUC_VALUE);
            byte[] data = reportJasperService.getJasperReportInBytes(
                option, strategy.getTemplate(), fileType, parameters,
                reportList
            );

            if (data == null) {
                return buildResponseWithError(
                    "Error al generar el reporte.",
                    HttpStatus.NOT_FOUND,
                    "Periodo: " + year + ", Opción: " + option
                        + ", Tipo de archivo: " + fileType
                );
            }

            ReportTypeEnum reportType = ReportTypeEnum.getReportTypeByCode(fileType);
            String fileName = strategy.getBaseFileName() + LocalDateTime.now()
                + reportType.getExtension();
            ByteArrayInputStream bis = new ByteArrayInputStream(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "force-download"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
            headers.setContentLength(data.length);

            InputStreamResource resource = new InputStreamResource(bis);
            return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (ResourceNotFoundException rnfe) {
            return buildResponseWithError(
                "No se encontró información para el periodo: " + year,
                HttpStatus.NOT_FOUND, rnfe.getMessage()
            );
        }
    }

    /**
     * Helper: find assets by period.
     *
     * @param period the accounting period
     * @return a ResponseEntity with assets or error
     */
    private ResponseEntity<ResponseData> handleFindByPeriod(String period, String username) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD);
        LocalDate periodDate = LocalDate.parse(period, formatter);
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodAndAddedByService(periodDate, username);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el periodo: "
                + period, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el periodo: "
                + period, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Helper: find assets by main fixed asset code.
     *
     * @param mainFixedAssetCode the main asset code
     * @return a ResponseEntity with assets or error
     */
    private ResponseEntity<ResponseData> handleFindByMainFixedAssetCode(String mainFixedAssetCode, String username) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para el código de activo fijo principal: "
                + mainFixedAssetCode, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el código de "
                + "activo fijo principal: " + mainFixedAssetCode, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Helper: find assets by accounting account.
     *
     * @param accountingAccount the accounting account code
     * @return a ResponseEntity with assets or error
     */
    private ResponseEntity<ResponseData> handleFindByAccountingAccount(String accountingAccount, String username) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username);

        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            return buildResponse("Se obtuvo informacion para la cuenta contable: "
                + accountingAccount, listFinancialFixedAsset, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para la cuenta contable: "
                + accountingAccount, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Builds a successful ResponseEntity with JSON body.
     *
     * @param message    the success message
     * @param obj        the response object or data
     * @param statusCode the HTTP status to return
     * @return a ResponseEntity containing the ResponseData
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
     * Builds an error ResponseEntity with JSON body.
     *
     * @param message the error message
     * @param status  the HTTP status to return
     * @param details additional error details
     * @return a ResponseEntity containing the ResponseData with exception info
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
