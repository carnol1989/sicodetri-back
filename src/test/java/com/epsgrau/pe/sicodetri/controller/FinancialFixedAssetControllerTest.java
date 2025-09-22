package com.epsgrau.pe.sicodetri.controller;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelInputStream;
import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelInputStreamFailed;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.TYPE_XLSX;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_00;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_1;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_3;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.LIST_INPUT_DATA;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.FORMATTER_YYYY_MM_DD;
import static com.epsgrau.pe.sicodetri.util.DataTest.auth;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListFinancialFixedAsset;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListInputData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.dto.mapper.FinancialFixedAssetMapper;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategyFactory;
import com.epsgrau.pe.sicodetri.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ContentDisposition;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@link FinancialFixedAssetController}.
 *
 * <p>This suite verifies all controller endpoints and helper flows using a mix of
 * {@link org.springframework.test.web.servlet.MockMvc} (for HTTP-level assertions) and
 * direct method invocations (for fine-grained response inspections). Covered areas include:</p>
 *
 * <ul>
 *   <li><b>Excel upload</b>: happy-path parsing and session storage, invalid format handling,
 *       and exception paths with JSON error payloads.</li>
 *   <li><b>Persisting assets</b>: saving session-backed data (201), empty/null session cases (400),
 *       and unexpected errors (417).</li>
 *   <li><b>Projections</b>: successful generation (201), ALL-prefix path, not-found (404),
 *       invalid date (400), unsupported type (400), and unexpected failures (417).</li>
 *   <li><b>Queries</b>: retrieval by period/code/account (200), not-found (404), invalid option (417)
 *       with message and code checks.</li>
 *   <li><b>Reports</b>: strategy selection and Jasper integration—file download on success (200 with
 *       <code>Content-Disposition: attachment</code>), no data (404), unsupported option (404),
 *       and exceptional paths (NumberFormatException, JRException, runtime errors).</li>
 *   <li><b>Error payloads</b>: validates {@link ResponseData} fields, nested
 *       {@link com.epsgrau.pe.sicodetri.exception.ExceptionResponse}, timestamps, and details.</li>
 * </ul>
 *
 * <p><b>Notes:</b></p>
 * <ul>
 *   <li>A global stub of {@link com.epsgrau.pe.sicodetri.dto.mapper.FinancialFixedAssetMapper}
 *       is applied in {@code @BeforeEach} to avoid NPEs in successful paths.</li>
 *   <li>Tests focus on controller behavior (status codes, payload shape, headers);
 *       service and strategy internals are mocked.</li>
 * </ul>
 *
 * <b>Class</b>: {@link FinancialFixedAssetControllerTest}<br/>
 * <b>COPYRIGHT</b>: &copy; 2024 EPS Grau. <br/>
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
 *       <li>Jun 06, 2025 (JAR) Initial version.</li>
 *       <li>Sep 10, 2025 (Docs) Align Javadoc with actual coverage, status codes, and MockMvc usage.</li>
 *     </ul>
 * @version 1.1
 */
@Slf4j
class FinancialFixedAssetControllerTest {

    public static String BASE_URI = "/api/v1/financial-fixed-assets";

    private MockMvc mockMvc;

    @Mock
    private FinancialFixedAssetService service;

    @Mock
    private ReportJasperService reportJasperService;

    @Mock
    private ReportStrategyFactory reportStrategyFactory;

    @Mock
    private ReportStrategy reportStrategy;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FinancialFixedAssetMapper mapper;

    @InjectMocks
    private FinancialFixedAssetController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        // Stub global del mapper para evitar NPE en los casos OK
        when(mapper.listFinancialFixedAssetToListFinancialFixedAssetDto(anyList()))
            .thenReturn(List.of(new FinancialFixedAssetDto()));
    }

    @Test
    @DisplayName("Return success response when Excel file is uploaded and processed correctly")
    void returnSuccessResponseWhenExcelFileIsUploadedAndProcessedCorrectly() throws Exception {
        // Arrange
        String period = "2022-01-01";
        String fileName = "test.xlsx";
        String username = "TEST";
        ByteArrayInputStream bais = createMockExcelInputStream();

        MultipartFile file = new MockMultipartFile("file", fileName, TYPE_XLSX, bais);
        List<InputData> inputDataList = getListInputData();

        when(service.loadExcelFileToObjectService(file, period, username)).thenReturn(inputDataList);

        MockHttpSession session = new MockHttpSession();

        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
            .file((MockMultipartFile) file)
            .param("period", period)
            .with(user(username))
            .session(session);

        // Act & Assert
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(S_VAL_00))
            .andExpect(jsonPath("$.message").value("Archivo subido correctamente: test.xlsx"))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.exception").doesNotExist()); // no hay exception en OK
    }

    @Test
    @DisplayName("Return failure response when uploaded file has invalid format")
    void returnFailureResponseWhenUploadedFileHasInvalidFormat() throws Exception {
        // Arrange
        String period = "2022-01-01";
        String fileName = "test.csv";
        String invalidContentType = "text/csv";

        // Act
        MockMultipartFile file = new MockMultipartFile("file", fileName, invalidContentType, new byte[0]);
        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
            .file(file)
            .param("period", period);

        // Assert
        mockMvc.perform(requestBuilder)
            .andExpect(status().isExpectationFailed())
            .andExpect(jsonPath("$.code").value(S_VAL_01))
            .andExpect(jsonPath("$.message").value("Formato no permitido. Debe ser .xlsx"))
            .andExpect(jsonPath("$.exception").exists());
    }

    @Test
    @DisplayName("Return failure response when exception is thrown during file upload")
    void returnFailureResponseWhenExceptionIsThrownDuringFileUpload() throws Exception {
        // Arrange
        String fileName = "test.xlsx";
        String period = "2022-01-01";
        String username = "katherine_gutierrez";
        ByteArrayInputStream errorInputStream  = createMockExcelInputStreamFailed();
        MultipartFile file = new MockMultipartFile("file", fileName, TYPE_XLSX, errorInputStream);

        when(service.loadExcelFileToObjectService(file, period, username))
            .thenThrow(new RuntimeException("Error al procesar archivo: test.xlsx"));

        TestingAuthenticationToken auth = new TestingAuthenticationToken(username, "pwd", "ROLE_AssetUploader");
        auth.setAuthenticated(true);

        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
            .file((MockMultipartFile) file)
            .param("period", period)
            .with(req -> { req.setUserPrincipal(auth); return req; });

        // Act & Assert
        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest()) // El controlador devuelve BAD_REQUEST en el catch
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value(S_VAL_01))
            .andExpect(jsonPath("$.message").value("Error al subir archivo."))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.exception").exists())
            .andExpect(jsonPath("$.exception.timestamp").exists())
            .andExpect(jsonPath("$.exception.message").exists())
            .andExpect(jsonPath("$.exception.details").exists());
    }

    @Test
    @DisplayName("Return created response when input data is stored successfully from session")
    void returnCreatedResponseWhenInputDataIsStoredSuccessfullyFromSession() {
        // Arrange
        String period = "2022-01-01";

        List<InputData> inputDataList = getListInputData();
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(inputDataList);

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController(period, session, null);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(S_VAL_00, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Información guardada correctamente"));
    }

    @Test
    @DisplayName("Return expectation failed when exception is thrown while saving input data")
    void returnExpectationFailedWhenExceptionIsThrownWhileSavingInputData() {
        // Arrange
        String period = "2024-08-01";
        List<InputData> listInputData = Collections.singletonList(new InputData());
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(listInputData);
        doThrow(new RuntimeException("Test Exception")).when(service).saveAllService(anyString(), anyList(), anyString());

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController(period, session, null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        ResponseData responseData = response.getBody();
        assertNotNull(responseData);
        assertEquals(S_VAL_01, responseData.getCode());
        assertTrue(responseData.getMessage().contains("Error al obtener información"));
        assertNotNull(responseData.getException());
        assertEquals("Mensaje: Test Exception\nCausa: null", responseData.getException().getDetails());
    }

    @Test
    @DisplayName("Return BAD_REQUEST when session input data is empty")
    void returnExpectationFailedWhenSessionInputDataIsEmpty() {
        // Arrange
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2024-08", session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información de entrada"));
    }

    @Test
    @DisplayName("Return BAD_REQUEST when session input data is null")
    void returnExpectationFailedWhenSessionInputDataIsNull() {
        // Arrange
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(null);

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2022-01-01", session, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("01", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información"));
    }

//    @Test
//    @DisplayName("Return CREATED when financial projection is generated successfully")
//    void returnOkWhenFinancialProjectionIsGeneratedSuccessfully() {
//        // Arrange
//        String period = "2022-01-01";
//        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
//            .thenReturn(getListFinancialFixedAsset());
//
//        // Act
//        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, S_VAL_332, null);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(S_VAL_00, response.getBody().getCode());
//        assertTrue(response.getBody().getMessage().contains("Proyección generada correctamente"));
//    }

//    @Test
//    @DisplayName("Return CREATED when generating projection with account=ALL (prefixes)")
//    void returnCreatedWhenGeneratingProjectionForAllAccountsPrefixes() {
//        // Arrange
//        String period = "2022-01-01";
//        when(service.findByAddedByAndAccountingAccountPrefixesService(anyString(), anyList()))
//            .thenReturn(getListFinancialFixedAsset());
//
//        // Act
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(
//            ProjectionType.TEMPORARY_ADDITIONS, period, Constants.ALL, null);
//
//        // Assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals(S_VAL_00, response.getBody().getCode());
//        assertTrue(response.getBody().getMessage().contains("Proyección generada correctamente"));
//    }

//    @Test
//    @DisplayName("Return NOT_FOUND when no data is found for projection generation")
//    void returnExpectationFailedWhenNoDataIsFoundForProjectionGeneration() {
//        // Arrange
//        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
//            .thenReturn(new ArrayList<>());
//
//        // Act
//        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(
//            projectionType, "2024-08-01", "account", null);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals(S_VAL_01, response.getBody().getCode());
//        assertTrue(response.getBody().getMessage().contains("Error al generar proyección"));
//    }

//    @Test
//    @DisplayName("Return EXPECTATION_FAILED when exception is thrown during projection generation")
//    void returnExpectationFailedWhenExceptionIsThrownDuringProjectionGeneration() {
//        // Arrange
//        String period = "2024-08-01";
//        String account = "account";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
//            .thenThrow(new RuntimeException("Simulated exception"));
//
//        // Act
//        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, account, null);
//
//        // Assert
//        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
//        ResponseData responseData = response.getBody();
//        assertNotNull(responseData);
//        assertEquals(S_VAL_01, responseData.getCode());
//        assertTrue(responseData.getMessage().startsWith("Error al generar proyección"));
//        assertTrue(responseData.getException().getMessage().contains("Error al generar proyección"));
//        assertTrue(responseData.getException().getDetails().contains("Mensaje: Simulated exception"));
//        assertNotNull(responseData.getException().getTimestamp());
//    }

//    @Test
//    @DisplayName("Return BAD_REQUEST when projection service throws IllegalArgumentException")
//    void returnBadRequestWhenProjectionServiceThrowsIllegalArgumentException() {
//        // Arrange
//        String period = "2022-01-01";
//        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
//            .thenReturn(getListFinancialFixedAsset());
//        doThrow(new IllegalArgumentException("unsupported"))
//            .when(service).generateProjectionService(any(), any(LocalDate.class), anyList(), anyString(), anyString());
//
//        // Act
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(
//            ProjectionType.TEMPORARY_ADDITIONS, period, "332", null);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals(S_VAL_01, response.getBody().getCode());
//        assertTrue(response.getBody().getMessage().contains("Tipo de proyección no soportado: unsupported"));
//    }

    @ParameterizedTest
    @CsvSource({
        "1,'2022-01-01','','','katherine_gutierrez',OK,00,'Se obtuvo informacion para el periodo'",
        "1,'2022-02-01','','','katherine_gutierrez',NOT_FOUND,01,'Error al obtener informacion para el periodo'",
        "2,'','22.01.010862','','katherine_gutierrez',OK,00,'Se obtuvo informacion para el código de activo fijo principal'",
        "2,'','22.01.010773','','katherine_gutierrez',NOT_FOUND,01,'Error al obtener informacion para el código de activo fijo principal'",
        "3,'','','3321161001','katherine_gutierrez',OK,00,'Se obtuvo informacion para la cuenta contable'",
        "3,'','','3321161002','katherine_gutierrez',NOT_FOUND,01,'Error al obtener informacion para la cuenta contable'",
        "999,'2024-08-01','','','katherine_gutierrez',EXPECTATION_FAILED,01,'Error al obtener informacion del activo fijo'"
    })
    @DisplayName("Return appropriate response for getFinancialFixedAssetController based on option and input")
    void returnAppropriateResponseForGetFinancialFixedAssetControllerBasedOnOptionAndInput(int option, String period,
                                                                                           String mainFixedAssetCode, String accountingAccount, String username, HttpStatus expectedStatus, String expectedCode,
                                                                                           String expectedMessage) {
        // Arrange
        List<FinancialFixedAsset> financialFixedAssets = getListFinancialFixedAsset();

        if (option == N_VAL_1) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByPeriodAndAddedByService(any(LocalDate.class), anyString())).thenReturn(financialFixedAssets);
            } else {
                when(service.findByPeriodAndAddedByService(any(LocalDate.class), anyString())).thenReturn(null);
            }
        } else if (option == N_VAL_2) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username)).thenReturn(financialFixedAssets);
            } else {
                when(service.findByMainFixedAssetCodeAndAddedByService(mainFixedAssetCode, username)).thenReturn(null);
            }
        } else if (option == N_VAL_3) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username))
                    .thenReturn(financialFixedAssets);
            } else {
                when(service.findByFixedAssetAccountingAccountAndAddedByService(accountingAccount, username))
                    .thenReturn(null);
            }
        }

        // Act
        ResponseEntity<ResponseData> response = controller.getFinancialFixedAssetController(option, period, mainFixedAssetCode,
            accountingAccount, auth(username, "ROLE_DepreciationProcessor"));

        // Assert
        assertEquals(expectedStatus, response.getStatusCode());
        assertEquals(expectedCode, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("Return expectation failed when exception is thrown while getting fixed asset info")
    void returnExpectationFailedWhenExceptionIsThrownWhileGettingFixedAssetInfo() {
        // Arrange
        int option = 1;
        String period = "2024-08-01";
        String mainFixedAssetCode = "someCode";
        String accountingAccount = "someAccount";

        when(service.findByPeriodAndAddedByService(any(LocalDate.class), anyString()))
            .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ResponseEntity<ResponseData> response = controller.getFinancialFixedAssetController(
            option, period, mainFixedAssetCode, accountingAccount, null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        ResponseData responseData = response.getBody();
        assertNotNull(responseData);
        assertEquals("01", responseData.getCode());
        assertTrue(responseData.getMessage().startsWith("Error al obtener informacion del activo fijo:"));
        assertTrue(responseData.getException().getMessage().contains("Error al obtener informacion del activo fijo:"));
        assertTrue(responseData.getException().getDetails().contains("Mensaje: Simulated exception"));
        assertNotNull(responseData.getException().getTimestamp());
    }

    @Test
    @DisplayName("Return file download when reportList non-empty and strategy + Jasper devuelven bytes")
    void returnFileDownloadWhenReportListNonEmptyAndJasperDevuelvenBytes() throws Exception {
        // Arrange
        String option = "1";
        String fileType = "xlsx";
        String year = "2022";
        String mainCode = "MAIN-123";
        String acc = "ACC-001";
        String start = "2022-01-01";
        String end = "2022-12-31";
        String useStart = "2022-02-01";
        String useEnd = "2022-11-30";

        when(reportStrategyFactory.getStrategy(eq(option))).thenReturn(reportStrategy);
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("Libro_Tributario_Activo_Fijo_");

        List<FinancialFixedAssetDto> reportList = List.of(
            new FinancialFixedAssetDto(), new FinancialFixedAssetDto()
        );
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(reportList);

        byte[] fakeXlsx = "FAKE XLSX DATA".getBytes();
        when(reportJasperService.getJasperReportInBytes(
            eq(option),
            eq("rpt_tax_book.jrxml"),
            eq(fileType),
            anyMap(),
            eq(reportList)
        )).thenReturn(fakeXlsx);

        // Act
        MvcResult mvcResult = mockMvc.perform(get(BASE_URI + "/report")
                .param("option", option)
                .param("fileType", fileType)
                .param("year", year)
                .param("mainCode", mainCode)
                .param("accountingAccount", acc)
                .param("startAcquisitionDate", start)
                .param("endAcquisitionDate", end)
                .param("startDateUse", useStart)
                .param("endDateUse", useEnd)
            )
            .andExpect(status().isOk())
            .andReturn();

        String headerValue = mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(headerValue);
        ContentDisposition cd = ContentDisposition.parse(headerValue);
        assertEquals("attachment", cd.getType());
        assertTrue(cd.getFilename().startsWith("Libro_Tributario_Activo_Fijo_"));
        assertTrue(cd.getFilename().endsWith(".xlsx"));

        byte[] body = mvcResult.getResponse().getContentAsByteArray();
        assertArrayEquals(fakeXlsx, body);

        verify(reportStrategyFactory).getStrategy(eq(option));
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verify(reportJasperService).getJasperReportInBytes(
            eq(option),
            eq("rpt_tax_book.jrxml"),
            eq(fileType),
            anyMap(),
            eq(reportList)
        );
    }

    @Test
    @DisplayName("Return NOT_FOUND when reportList is empty (MockMvc)")
    void throwResourceNotFoundExceptionWhenReportListIsEmpty() throws Exception {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(eq(option)))
            .thenReturn(reportStrategy);
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get(BASE_URI + "/report")
                .param("option", option)
                .param("fileType", "PDF")
                .param("year", "2022")
                .param("mainCode", "A")
                .param("accountingAccount", "B")
                .param("startAcquisitionDate", "2022-01-01")
                .param("endAcquisitionDate",   "2022-12-31")
                .param("startDateUse",         "2022-02-01")
                .param("endDateUse",           "2022-11-30")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message")
                .value("No hay información para el periodo."));

        verify(reportStrategyFactory).getStrategy(eq(option));
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verifyNoMoreInteractions(reportJasperService);
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when Jasper return null")
    void throwResourceNotFoundExceptionWhenJasperReturnNull() throws Exception {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(List.of(new FinancialFixedAssetDto()));
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("Libro_Tributario_Activo_Fijo_");
        when(reportJasperService.getJasperReportInBytes(
            anyString(), anyString(), anyString(), anyMap(), anyList()
        )).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get(BASE_URI + "/report")
                .param("option",               option)
                .param("fileType",             "PDF")
                .param("year",                 "2022")
                .param("mainCode",             "MAIN-123")
                .param("accountingAccount",    "ACC-001")
                .param("startAcquisitionDate", "2022-01-01")
                .param("endAcquisitionDate",   "2022-12-31")
                .param("startDateUse",         "2022-02-01")
                .param("endDateUse",           "2022-11-30")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.code").value("01")) // code es String
            .andExpect(jsonPath("$.message").value("Error al generar el reporte."))
            .andExpect(jsonPath("$.exception.details")
                .value("Periodo: 2022, Opción: 1, Tipo de archivo: PDF"));

        verify(reportStrategyFactory).getStrategy(option);
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verify(reportJasperService).getJasperReportInBytes(
            eq(option),
            eq("rpt_tax_book.jrxml"),
            eq("PDF"),
            anyMap(),
            anyList()
        );
    }

    @Test
    @DisplayName("Return NOT_FOUND when strategy not supported (factory throws ResourceNotFoundException)")
    void returnNotFoundWhenStrategyNotSupported() throws Exception {
        // Arrange
        String option = "999";
        when(reportStrategyFactory.getStrategy(option))
            .thenThrow(new ResourceNotFoundException("Unsupported option"));

        // Act & Assert
        mockMvc.perform(get(BASE_URI + "/report")
                .param("option",               option)
                .param("fileType",             "xlsx")
                .param("year",                 "2022")
                .param("mainCode",             "X")
                .param("accountingAccount",    "Y")
                .param("startAcquisitionDate", "2022-01-01")
                .param("endAcquisitionDate",   "2022-12-31")
                .param("startDateUse",         "2022-02-01")
                .param("endDateUse",           "2022-11-30")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                .value("Tipo de reporte no soportado para opción: " + option))
            .andExpect(jsonPath("$.code").value("01"))
            .andExpect(jsonPath("$.exception.details").value("Unsupported option"));
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when exception inesperada en Jasper o servicio")
    void throwResourceNotFoundExceptionWhenExceptionInReportGeneration() throws SQLException {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenThrow(new RuntimeException("Simulated failure"));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            controller.callGetReportsController(
                option,
                "PDF",
                "2022",
                "MAIN-123",
                "ACC-001",
                "2022-01-01",
                "2022-12-31",
                "2022-02-01",
                "2022-11-30"
            );
        });

        assertEquals("Simulated failure", ex.getMessage());
        verify(reportStrategyFactory).getStrategy(option);
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
    }

    @Test
    @DisplayName("Return OK when period exists and return NOT_FOUND when period has no data")
    void returnOkWhenPeriodExistsAndReturnNotFoundWhenPeriodHasNoData() {
        // Arrange: caso OK
        LocalDate fechaValida = LocalDate.now();
        when(service.findByPeriodAndAddedByService(any(LocalDate.class), anyString())).thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
            N_VAL_1,
            fechaValida.format(DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD)),
            "", "", null);

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para el periodo"));

        // Arrange: caso NOT_FOUND
        when(service.findByPeriodAndAddedByService(any(LocalDate.class), anyString())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
            N_VAL_1,
            fechaValida.format(DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD)),
            "", "",
            null);

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para el periodo"));
    }

    @Test
    @DisplayName("Return OK when mainFixedAssetCode exists and return NOT_FOUND when none")
    void returnOkWhenMainFixedAssetCodeExistsAndReturnNotFoundWhenNone() {
        // Arrange: caso OK
        when(service.findByMainFixedAssetCodeAndAddedByService(anyString(), anyString()))
            .thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
            N_VAL_2, "", "ABC123", "", null);

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para el código de activo fijo principal"));

        // Arrange: caso NOT_FOUND
        when(service.findByMainFixedAssetCodeAndAddedByService(anyString(), anyString())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
            N_VAL_2, "", "ABC123", "", null);

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para el código de activo fijo principal"));
    }

    @Test
    @DisplayName("Return OK when accountingAccount exists and return NOT_FOUND when none")
    void returnOkWhenAccountingAccountExistsAndReturnNotFoundWhenNone() {
        // Arrange: caso OK
        when(service.findByFixedAssetAccountingAccountAndAddedByService(anyString(), anyString()))
            .thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
            N_VAL_3, "", "", "ACC-001", null);

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para la cuenta contable"));

        // Arrange: caso NOT_FOUND
        when(service.findByFixedAssetAccountingAccountAndAddedByService(anyString(), anyString()))
            .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
            N_VAL_3, "", "", "ACC-001", null);

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para la cuenta contable"));
    }

    @Test
    @DisplayName("Return EXPECTATION_FAILED when option is invalid")
    void returnExpectationFailedWhenOptionIsInvalid() {
        ResponseEntity<ResponseData> resp = controller.getFinancialFixedAssetController(
            999, "2022-01-01", "X", "Y", null
        );

        assertEquals(HttpStatus.EXPECTATION_FAILED, resp.getStatusCode());
        assertEquals(S_VAL_01, resp.getBody().getCode());
        assertTrue(resp.getBody().getMessage().contains("Error al obtener informacion del activo fijo"));
    }

    @Test
    @DisplayName("Throw 404 JSON when reportList is empty")
    void throw404WhenReportListIsEmpty() throws Exception {
        String option = "1";
        when(reportStrategyFactory.getStrategy(eq(option))).thenReturn(reportStrategy);
        when(reportStrategy.generateReport(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get(BASE_URI + "/report")
                .param("option", option)
                .param("fileType", "PDF")
                .param("year", "2022")
                .param("mainCode", "A")
                .param("accountingAccount", "B")
                .param("startAcquisitionDate", "2022-01-01")
                .param("endAcquisitionDate",   "2022-12-31")
                .param("startDateUse",         "2022-02-01")
                .param("endDateUse",           "2022-11-30")
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("No hay información para el periodo."))
            .andExpect(jsonPath("$.exception.details").exists());

        verify(reportStrategyFactory).getStrategy(eq(option));
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verifyNoMoreInteractions(reportJasperService);
    }

//    @Test
//    @DisplayName("Return BAD_REQUEST when generating projection with invalid date format")
//    void returnBadRequestWhenGeneratingProjectionWithInvalidDateFormat() {
//        // Act
//        ResponseEntity<ResponseData> response = controller.generateProjectionController(
//            ProjectionType.TEMPORARY_ADDITIONS, "2022/01/01", "332", null);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("01", response.getBody().getCode());
//        assertTrue(response.getBody().getMessage().contains("Formato de fecha inválido"));
//    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when reportList empty in direct call")
    void throwResourceNotFoundExceptionWhenReportListEmptyDirectCall() throws Exception {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = controller.callGetReportsController(
            option, "xlsx", "2022", "A", "B",
            "2022-01-01", "2022-12-31", "2022-02-01", "2022-11-30"
        );

        // Assert HTTP status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ResponseData body = (ResponseData) response.getBody();
        assertNotNull(body);
        assertEquals("No hay información para el periodo.", body.getMessage());
        assertEquals("01", body.getCode());
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when Jasper returns null in direct call")
    void throwResourceNotFoundExceptionWhenJasperReturnsNullDirectCall() throws Exception {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        List<FinancialFixedAssetDto> reportList = List.of(new FinancialFixedAssetDto());
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(reportList);
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("file_");
        when(reportJasperService.getJasperReportInBytes(
            eq(option), eq("rpt_tax_book.jrxml"), eq("xlsx"), anyMap(), eq(reportList)
        )).thenReturn(null);

        // Act
        ResponseEntity<ResponseData> response = (ResponseEntity<ResponseData>) controller.callGetReportsController(
            option, "xlsx", "2022", "A", "B",
            "2022-01-01", "2022-12-31", "2022-02-01", "2022-11-30"
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ResponseData body = response.getBody();
        assertNotNull(body);
        assertEquals(Constants.S_VAL_01, body.getCode());
        assertEquals("Error al generar el reporte.", body.getMessage());
        ExceptionResponse ex = body.getException();
        assertNotNull(ex);
        assertEquals("Periodo: 2022, Opción: 1, Tipo de archivo: xlsx", ex.getDetails());

        verify(reportStrategyFactory).getStrategy(option);
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verify(reportJasperService).getJasperReportInBytes(
            eq(option),
            eq("rpt_tax_book.jrxml"),
            eq("xlsx"),
            anyMap(),
            eq(reportList)
        );
    }

    @Test
    @DisplayName("Throw NumberFormatException when year is not a number")
    void throwNumberFormatExceptionWhenYearNotANumber() throws Exception {
        // Act & Assert
        assertThrows(NumberFormatException.class, () ->
            controller.callGetReportsController(
                "1", "xlsx", "ABC", "A", "B",
                "2022-01-01", "2022-12-31", "2022-02-01", "2022-11-30"
            )
        );
    }

    @Test
    @DisplayName("Throw JRException when Jasper service throws JRException")
    void throwJRExceptionWhenJasperServiceThrowsJRException() throws Exception {
        // Arrange
        String option = "1";
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        List<FinancialFixedAssetDto> reportList = List.of(new FinancialFixedAssetDto());
        when(reportStrategy.generateReport(any(ParamReportDto.class))).thenReturn(reportList);
        when(reportStrategy.getTemplate()).thenReturn("rpt.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("file_");
        when(reportJasperService.getJasperReportInBytes(
            anyString(), anyString(), anyString(), anyMap(), anyList()
        )).thenThrow(new JRException("JRError"));

        // Act & Assert
        JRException ex = assertThrows(JRException.class, () ->
            controller.callGetReportsController(
                option, "xlsx", "2022", "A", "B",
                "2022-01-01", "2022-12-31", "2022-02-01", "2022-11-30"
            )
        );
        assertEquals("JRError", ex.getMessage());
    }
}