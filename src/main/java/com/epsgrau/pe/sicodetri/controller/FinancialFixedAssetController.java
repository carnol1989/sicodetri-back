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
import com.epsgrau.pe.sicodetri.dto.mapper.FinancialFixedAssetMapper;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategyFactory;
import com.epsgrau.pe.sicodetri.util.Constants;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
 * <p>
 * Exposes endpoints to:
 * </p>
 * <ul>
 *   <li>Upload Excel (Formato 7) and keep data in session</li>
 *   <li>Persist uploaded assets</li>
 *   <li>Generate projections (additions / deductions)</li>
 *   <li>Query assets by period, main asset code or accounting account</li>
 *   <li>Generate reports (e.g., tax book) as downloadable files</li>
 * </ul>
 *
 * <b>Class</b>: {@link FinancialFixedAssetController}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 06, 2024 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
@Tag(name = "Financial Fixed Asset", description = "Gestión de activos fijos "
    + "financieros y sus proyecciones")
@SecurityRequirement(name = "oauth2")
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

    @Autowired
    private FinancialFixedAssetMapper mapper;

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
     * Uploads an Excel file containing fixed-asset input data (Formato 7) and stores
     * the parsed rows in the current HTTP session.
     *
     * <p><b>Returns</b>:</p>
     * <ul>
     *   <li><b>200 OK</b> – file parsed and data stored in session; body is {@link ResponseData}.</li>
     *   <li><b>417 Expectation Failed</b> – file has invalid format (not .xlsx); body is {@link ResponseData}.</li>
     *   <li><b>400 Bad Request</b> – unexpected error while processing upload; body is {@link ResponseData}.</li>
     * </ul>
     *
     * @param file    the uploaded Excel file (.xlsx)
     * @param period  the accounting period in {@code YYYY-MM-DD} format
     * @param request the current HTTP request (used to access the session)
     * @param authentication current principal (used to log the username), may be {@code null}
     * @return a {@link ResponseEntity} with a {@link ResponseData} payload describing the outcome
     */
    @Operation(
        summary = "Cargar archivo Excel con datos de activos fijos (Formato 7)",
        description = "Recibe un archivo .xlsx (campo form-data: 'file') y el periodo "
            + "contable ('period') y guarda los registros parseados en la sesión."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Archivo subido y parseado correctamente. Los registros quedan en sesión.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "417",
                description = "Formato no permitido (debe ser .xlsx).",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Error inesperado procesando el archivo.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Prohibido.")})
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
     * Persists previously uploaded fixed-asset data stored in the session for the given period.
     * Existing records for the period and user are deleted and replaced with the uploaded data.
     *
     * <p><b>Returns</b>:</p>
     * <ul>
     *   <li><b>201 Created</b> – data saved successfully; body is {@link ResponseData}.</li>
     *   <li><b>400 Bad Request</b> – no uploaded data in session for the period; body is {@link ResponseData}.</li>
     *   <li><b>417 Expectation Failed</b> – unexpected error while saving; body is {@link ResponseData}.</li>
     * </ul>
     *
     * @param period  the accounting period in {@code YYYY-MM-DD} format
     * @param session the current HTTP session that must contain the uploaded data
     * @param authentication current principal (used to scope data by user), may be {@code null}
     * @return a {@link ResponseEntity} with a {@link ResponseData} payload describing the outcome
     */
    @Operation(
        summary = "Guardar activos fijos previamente cargados en sesión",
        description = "Elimina los registros del periodo/usuario y persiste los nuevos cargados vía /upload."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Información guardada correctamente.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "400",
                description = "No hay datos cargados en sesión para el periodo.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "417",
                description = "Error inesperado al guardar.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Prohibido.")})
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
     * Generates a projection (e.g., temporary additions or deductions) for the selected
     * set of fixed assets, filtered by account prefix or for all supported prefixes.
     *
     * <p><b>Returns</b>:</p>
     * <ul>
     *   <li><b>201 Created</b> – projection generated; body is {@link ResponseData} with processed count.</li>
     *   <li><b>404 Not Found</b> – no assets found for the given filters; body is {@link ResponseData}.</li>
     *   <li><b>400 Bad Request</b> – invalid date format or unsupported projection
     *   type; body is {@link ResponseData}.</li>
     *   <li><b>417 Expectation Failed</b> – unexpected error while processing; body is {@link ResponseData}.</li>
     * </ul>
     *
     * @param type    the projection type (e.g., {@code TEMPORARY_DEDUCTION}, {@code TEMPORARY_ADDITION})
     * @param period  the accounting period in {@code YYYY-MM-DD} format
     * @param account the accounting account prefix, or {@code ALL} to process predefined prefixes
     * @param authentication current principal (used to scope data by user), may be {@code null}
     * @return a {@link ResponseEntity} with a {@link ResponseData} payload describing the outcome
     */
    @Operation(
        summary = "Generar proyección de Activos Fijos",
        description = "Genera adiciones/deducciones temporales según tipo, periodo "
            + "y cuenta (o ALL para prefijos 332–337)."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "201",
                description = "Proyección generada correctamente.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "404",
                description = "No se encontraron activos para los filtros indicados.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Fecha inválida (YYYY-MM-DD) o tipo de proyección no soportado.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "417",
                description = "Error inesperado al generar la proyección.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Prohibido.")})
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
     * Retrieves fixed-asset information. By one of the supported search options:
     * <ul>
     *   <li><b>1</b> — by accounting period</li>
     *   <li><b>2</b> — by main fixed-asset code</li>
     *   <li><b>3</b> — by accounting account</li>
     * </ul>
     *
     * <p><b>Returns</b>:</p>
     * <ul>
     *   <li><b>200 OK</b> – assets found; body is {@link ResponseData} with a list of DTOs.</li>
     *   <li><b>404 Not Found</b> – no results for the provided criteria; body is {@link ResponseData}.</li>
     *   <li><b>417 Expectation Failed</b> – invalid option or unexpected error; body is {@link ResponseData}.</li>
     * </ul>
     *
     * @param option search option (1 = period, 2 = main fixed-asset code, 3 = accounting account)
     * @param period accounting period (used when {@code option=1}) in {@code YYYY-MM-DD} format
     * @param mainFixedAssetCode main fixed-asset code (used when {@code option=2})
     * @param accountingAccount accounting account (used when {@code option=3})
     * @param authentication current principal (used to scope data by user), may be {@code null}
     * @return a {@link ResponseEntity} wrapping {@link ResponseData} with results or an error
     */
    @Operation(
        summary = "Obtener información de activos fijos",
        description = "option: 1=por periodo (requiere 'period'), 2=por código "
            + "activo fijo principal (requiere 'mainFixedAssetCode'), "
            + "3=por cuenta contable (requiere 'accountingAccount')."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Resultados encontrados.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "404",
                description = "Sin resultados para los criterios indicados.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "400",
                description = "Parámetros inválidos (p.ej. fecha con formato incorrecto).",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "417",
                description = "Opción inválida u otro error inesperado.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Prohibido.")})
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
     * Generates and returns a report (e.g., Tax Book) based on the given option and parameters.
     * On success, returns a downloadable file stream; otherwise returns a JSON error payload.
     *
     * <p><b>Returns</b>:</p>
     * <ul>
     *   <li><b>200 OK</b> – body is the report file as {@code application/octet-stream} with
     *       {@code Content-Disposition: attachment}.</li>
     *   <li><b>404 Not Found</b> – unsupported report option or no data for the
     *   requested period; JSON {@link ResponseData}.</li>
     * </ul>
     *
     * @param option report option selector (delegated to {@link ReportStrategyFactory})
     * @param fileType output format (e.g., {@code xlsx})
     * @param year accounting year
     * @param mainCode main fixed-asset code filter
     * @param accountingAccount accounting account code filter
     * @param startAcquisitionDate acquisition start date ({@code YYYY-MM-DD})
     * @param endAcquisitionDate acquisition end date ({@code YYYY-MM-DD})
     * @param startDateUse start date of use ({@code YYYY-MM-DD})
     * @param endDateUse end date of use ({@code YYYY-MM-DD})
     * @return a file stream on success; otherwise a JSON {@link ResponseData} with error details
     * @throws JRException if Jasper report processing fails
     * @throws FileNotFoundException if the report template is missing
     * @throws SQLException if a database error occurs during report generation
     */
    @Operation(
        summary = "Descargar reporte del Libro Tributario / Detalles",
        description = "Genera y descarga el archivo (xlsx, pdf, etc.) según la opción "
            + "de reporte y filtros. Si no hay datos o la opción no existe, retorna JSON con el error."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Archivo generado correctamente.",
                content = @Content(
                    mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")
                )),
            @ApiResponse(
                responseCode = "404",
                description = "Tipo de reporte no soportado o sin información para el periodo.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno al generar el reporte (JRException/SQLException/plantilla faltante).",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseData.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado."),
            @ApiResponse(responseCode = "403", description = "Prohibido.")})
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
     * Helper: find assets by period for the current user.
     *
     * @param period accounting period in {@code YYYY-MM-DD} format
     * @param username current user (used to scope data)
     * @return a {@link ResponseEntity} with {@link ResponseData} containing DTO list or error
     */
    private ResponseEntity<ResponseData> handleFindByPeriod(String period, String username) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATTER_YYYY_MM_DD);
        LocalDate periodDate = LocalDate.parse(period, formatter);
        List<FinancialFixedAsset> listFinancialFixedAsset = service.findByPeriodAndAddedByService(periodDate, username);
        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            List<FinancialFixedAssetDto> listDtos = mapper.listFinancialFixedAssetToListFinancialFixedAssetDto(
                listFinancialFixedAsset);
            return buildResponse("Se obtuvo informacion para el periodo: "
                + period, listDtos, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el periodo: "
                + period, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Helper: find assets by main fixed-asset code for the current user.
     *
     * @param mainFixedAssetCode main fixed-asset code
     * @param username current user (used to scope data)
     * @return a {@link ResponseEntity} with {@link ResponseData} containing DTO list or error
     */
    private ResponseEntity<ResponseData> handleFindByMainFixedAssetCode(String mainFixedAssetCode, String username) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username);
        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            List<FinancialFixedAssetDto> listDtos = mapper.listFinancialFixedAssetToListFinancialFixedAssetDto(
                listFinancialFixedAsset);
            return buildResponse("Se obtuvo informacion para el código de activo fijo principal: "
                + mainFixedAssetCode, listDtos, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para el código de "
                + "activo fijo principal: " + mainFixedAssetCode, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Helper: find assets by accounting account for the current user.
     *
     * @param accountingAccount accounting account code
     * @param username current user (used to scope data)
     * @return a {@link ResponseEntity} with {@link ResponseData} containing DTO list or error
     */
    private ResponseEntity<ResponseData> handleFindByAccountingAccount(String accountingAccount, String username) {
        List<FinancialFixedAsset> listFinancialFixedAsset =
            service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username);
        if (listFinancialFixedAsset != null && !listFinancialFixedAsset.isEmpty()) {
            List<FinancialFixedAssetDto> listDtos = mapper.listFinancialFixedAssetToListFinancialFixedAssetDto(
                listFinancialFixedAsset);
            return buildResponse("Se obtuvo informacion para la cuenta contable: "
                + accountingAccount, listDtos, HttpStatus.OK);
        } else {
            return buildResponseWithError("Error al obtener informacion para la cuenta contable: "
                + accountingAccount, HttpStatus.NOT_FOUND, "");
        }
    }

    /**
     * Builds a successful JSON {@link ResponseEntity} with a {@link ResponseData} body.
     *
     * @param message success message
     * @param obj     response payload (data)
     * @param statusCode HTTP status to return
     * @return a JSON {@link ResponseEntity} with {@link ResponseData}
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
     * Builds an error JSON {@link ResponseEntity} with a {@link ResponseData} body containing exception details.
     *
     * @param message error summary
     * @param status  HTTP status to return
     * @param details additional error details to include
     * @return a JSON {@link ResponseEntity} with {@link ResponseData} (error)
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
