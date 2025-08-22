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
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
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
 * <p>
 * Exercises all controller endpoints including:
 * <ul>
 *   <li>Excel upload and parsing</li>
 *   <li>Saving financial fixed assets</li>
 *   <li>Projection generation</li>
 *   <li>Retrieving fixed assets by period, code, or account</li>
 *   <li>Report generation via strategies and Jasper service</li>
 *   <li>Error handling and response content</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link FinancialFixedAssetControllerTest}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
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

    @InjectMocks
    private FinancialFixedAssetController controller;

    /**
     * Initializes Mockito mocks and configures MockMvc with the controller
     * and global exception handler before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /**
     * Verifies that uploading a valid Excel file returns HTTP 200
     * with a success JSON payload indicating the file name and code.
     *
     * @throws Exception if MockMvc request fails
     */
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
                .session(session);

        // Act
        MvcResult result = mockMvc.perform(requestBuilder).andReturn(); // Capturar el resultado

        // Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(S_VAL_00))
                .andExpect(jsonPath("$.message").value("Archivo subido correctamente: test.xlsx"))
                .andExpect(jsonPath("$.timestamp").exists()) // Validar que timestamp existe
                .andExpect(jsonPath("$.exception").isEmpty()) // Validar que `exception` es un objeto
                .andExpect(jsonPath("$.exception.timestamp").doesNotExist()) // Validar campo timestamp del objeto `ExceptionResponse` sean null
                .andExpect(jsonPath("$.exception.message").doesNotExist()) // Validar campo message del objeto `ExceptionResponse` sean null
                .andExpect(jsonPath("$.exception.details").doesNotExist()); // Validar campo details del objeto `ExceptionResponse` sean null
    }

    /**
     * Verifies that uploading a non-XLSX file returns HTTP 417 (Expectation Failed)
     * with an error code and appropriate error message.
     *
     * @throws Exception if MockMvc request fails
     */
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

    /**
     * Verifies that an exception during upload processing triggers HTTP 417
     * and returns JSON with exception details.
     *
     * @throws Exception if MockMvc request fails
     */
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

        // Act
        MvcResult result = mockMvc.perform(requestBuilder)
                .andReturn();

        // Assert
        mockMvc.perform(requestBuilder)
                .andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(S_VAL_01))
                .andExpect(jsonPath("$.message").value("Error al subir archivo."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.exception").exists())
                .andExpect(jsonPath("$.exception.timestamp").exists())
                .andExpect(jsonPath("$.exception.message").exists())
                .andExpect(jsonPath("$.exception.details").exists());
    }

    /**
     * Tests saving the previously uploaded data from session into the database,
     * expecting HTTP 201 Created and a success code/message.
     */
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

    /**
     * Verifies that an exception during saving input data returns HTTP 417
     * and JSON containing the exception details.
     */
    @Test
    @DisplayName("Return expectation failed when exception is thrown while saving input data")
    void returnExpectationFailedWhenExceptionIsThrownWhileSavingInputData() {
        // Arrange
        String period = "2024-08-01";
        List<InputData> listInputData = Collections.singletonList(new InputData()); // Simula datos de entrada
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

    /**
     * Verifies that empty session data for save returns HTTP 417
     * with an appropriate error message.
     */
    @Test
    @DisplayName("Return expectation failed when session input data is empty")
    void returnExpectationFailedWhenSessionInputDataIsEmpty() {
        // Arrange
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2024-08", session, null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información de entrada"));
    }

    /**
     * Tests successful temporary additions projection generation,
     * expecting HTTP 200 and a success code.
     */
    @Test
    @DisplayName("Return OK when financial projection is generated successfully")
    void returnOkWhenFinancialProjectionIsGeneratedSuccessfully() {
        // Arrange
        String period = "2022-01-01";
        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
                .thenReturn(getListFinancialFixedAsset());

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, S_VAL_332, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(S_VAL_00, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Proyección generada correctamente"));
    }

    /**
     * Verifies that projection generation with no data returns HTTP 417
     * and an error code/message.
     */
    @Test
    @DisplayName("Return expectation failed when no data is found for projection generation")
    void returnExpectationFailedWhenNoDataIsFoundForProjectionGeneration() {
        // Arrange
        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
            .thenReturn(new ArrayList<>());

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, "2024-08-01",
            "account", null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al generar proyección"));
    }

    /**
     * Verifies that an exception during projection generation returns HTTP 417
     * and JSON with exception details.
     */
    @Test
    @DisplayName("Return expectation failed when exception is thrown during projection generation")
    void returnExpectationFailedWhenExceptionIsThrownDuringProjectionGeneration() {
        // Arrange
        String period = "2024-08-01";
        String account = "account";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(service.findByPeriodAndAddedByAndAccountStartingWithService(any(LocalDate.class), anyString(), anyString()))
                .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, account, null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        ResponseData responseData = response.getBody();
        assertNotNull(responseData);
        assertEquals(S_VAL_01, responseData.getCode());
        assertTrue(responseData.getMessage().startsWith("Error al generar proyección"));
        assertTrue(responseData.getException().getMessage().contains("Error al generar proyección"));
        assertTrue(responseData.getException().getDetails().contains("Mensaje: Simulated exception"));
        assertNotNull(responseData.getException().getTimestamp());
    }

    /**
     * Parameterized test covering retrieval by option:
     * period, main asset code, or account number.
     * Verifies correct HTTP status, code, and message for each scenario.
     *
     * @param option          retrieval option (1=period,2=mainCode,3=account)
     * @param period          date period string
     * @param mainFixedAssetCode  asset code when option=2
     * @param accountingAccount   account when option=3
     * @param expectedStatus  expected HTTP status
     * @param expectedCode    expected response code
     * @param expectedMessage expected substring in response message
     */
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

    /**
     * Verifies that an exception in service.findByPeriodService results in HTTP 417
     * with exception details in JSON.
     */
    @Test
    @DisplayName("Return expectation failed when exception is thrown while getting fixed asset info")
    void returnExpectationFailedWhenExceptionIsThrownWhileGettingFixedAssetInfo() {
        // Arrange
        int option = 1;
        String period = "2024-08-01";
        String mainFixedAssetCode = "someCode";
        String accountingAccount = "someAccount";

        // Simulate the service to throw an exception
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

    /**
     * Tests successful report download when strategy and Jasper service return bytes.
     *
     * @throws Exception if MockMvc request fails
     */
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

        // 1) fábrica → estrategia
        when(reportStrategyFactory.getStrategy(eq(option))).thenReturn(reportStrategy);
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("Libro_Tributario_Activo_Fijo_");

        // 2) estrategia → lista no vacía
        List<FinancialFixedAssetDto> reportList = List.of(
            new FinancialFixedAssetDto(), new FinancialFixedAssetDto()
        );
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(reportList);

        // 3) Jasper → fake XLSX
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

        // Parse Content-Disposition
        String headerValue = mvcResult.getResponse().getHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(headerValue);
        ContentDisposition cd = ContentDisposition.parse(headerValue);
        assertEquals("attachment", cd.getType());
        assertTrue(cd.getFilename().startsWith("Libro_Tributario_Activo_Fijo_"));
        assertTrue(cd.getFilename().endsWith(".xlsx"));

        // Body
        byte[] body = mvcResult.getResponse().getContentAsByteArray();
        assertArrayEquals(fakeXlsx, body);

        // Verificaciones
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

    /**
     * Verifies that an empty report list yields HTTP 404 Not Found with JSON error.
     *
     * @throws Exception if MockMvc request fails
     */
    @Test
    @DisplayName("Throw ResourceNotFoundException when reportList is empty")
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
            // ahora ya sí podemos leer $.message
            .andExpect(jsonPath("$.message")
                .value("No hay información para el periodo."));

        // y por si acaso:
        verify(reportStrategyFactory).getStrategy(eq(option));
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verifyNoMoreInteractions(reportJasperService);
    }

    /**
     * Verifies that a null byte array from Jasper triggers HTTP 404
     * with detailed JSON error.
     *
     * @throws Exception if MockMvc request fails
     */
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
            // Si tu ResponseData incluye timestamp:
            .andExpect(jsonPath("$.timestamp").exists())
            // Nuevo: comprobamos el código que tu DTO monta
            .andExpect(jsonPath("$.code").value(1))
            .andExpect(jsonPath("$.message")
                .value("Error al generar el reporte."))
            // Ahora sí comprobamos el campo details
            .andExpect(jsonPath("$.exception.details")
                .value("Periodo: 2022, Opción: 1, Tipo de archivo: PDF"));

        // Verificaciones de invocaciones
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

    /**
     * Verifies that an unexpected exception in report strategy
     * bubbles through as RuntimeException from the controller.
     *
     * @throws SQLException if underlying JDBC error is simulated
     */
    @Test
    @DisplayName("Throw ResourceNotFoundException when exception inesperada en Jasper o servicio")
    void throwResourceNotFoundExceptionWhenExceptionInReportGeneration() throws SQLException {
        // Arrange
        String option            = "1";
        // 1) Stub de la fábrica para devolver tu estrategia mockeada
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        // 2) Stub de la estrategia para que su generateReport lance RuntimeException
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
        }, "Si la estrategia falla, debe lanzarse RuntimeException desde el wrapper del controlador");

        // El wrapper en el controlador usa exactamente este mensaje:
        assertEquals("Simulated failure", ex.getMessage());

        // Verificamos que realmente se invocó la estrategia
        verify(reportStrategyFactory).getStrategy(option);
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
    }

    /**
     * Tests getFinancialFixedAssetController: OK when data exists, NOT_FOUND when none.
     */
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

    /**
     * Tests getFinancialFixedAssetController: OK when mainFixedAssetCode exists, NOT_FOUND when none.
     */
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

    /**
     * Tests getFinancialFixedAssetController: OK when accountingAccount exists, NOT_FOUND when none.
     */
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

    /**
     * Verifies that an invalid option returns HTTP 417 with an error message.
     */
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

    /**
     * Verifies that an empty report list in JSON mode returns HTTP 404
     * with JSON error and exception details.
     *
     * @throws Exception if MockMvc request fails
     */
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

    /**
     * Verifies that null session data returns HTTP 417 and error code/message.
     */
    @Test
    @DisplayName("Return EXPECTATION_FAILED when session input data is null")
    void returnExpectationFailedWhenSessionInputDataIsNull() {
        // Arrange
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(null);

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2022-01-01", session, null);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals("01", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información"));
    }

    /**
     * Verifies that invalid date format for projection returns HTTP 400
     * with a bad-request code/message.
     */
    @Test
    @DisplayName("Return BAD_REQUEST when generating projection with invalid date format")
    void returnBadRequestWhenGeneratingProjectionWithInvalidDateFormat() {
        // Act
        ResponseEntity<ResponseData> response = controller.generateProjectionController(
            ProjectionType.TEMPORARY_ADDITIONS, "2022/01/01", "332", null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("01", response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Formato de fecha inválido"));
    }

    /**
     * Verifies that calling the controller’s direct-report method with empty data
     * returns HTTP 404 and a proper error body.
     */
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

        // Cast correcto a ResponseData
        ResponseData body = (ResponseData) response.getBody();
        assertNotNull(body, "El cuerpo no debe ser null");
        assertEquals("No hay información para el periodo.", body.getMessage());
        assertEquals("01", body.getCode());
    }

    /**
     * Verifies that a null Jasper byte array in the direct-call API returns HTTP 404
     * and structured JSON error details.
     */
    @Test
    @DisplayName("Throw ResourceNotFoundException when Jasper returns null in direct call")
    void throwResourceNotFoundExceptionWhenJasperReturnsNullDirectCall() throws Exception {
        // Arrange
        String option = "1";
        // 1) Fábrica → estrategia mockeada
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);
        // 2) Estrategia → lista NO vacía
        List<FinancialFixedAssetDto> reportList = List.of(new FinancialFixedAssetDto());
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(reportList);
        // 3) Template y nombre base
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("file_");
        // 4) Jasper devuelve null → devolverá ResponseEntity de error
        when(reportJasperService.getJasperReportInBytes(
            eq(option), eq("rpt_tax_book.jrxml"), eq("xlsx"), anyMap(), eq(reportList)
        )).thenReturn(null);

        // Act
        ResponseEntity<ResponseData> response = (ResponseEntity<ResponseData>) controller.callGetReportsController(
            option, "xlsx", "2022", "A", "B",
            "2022-01-01", "2022-12-31", "2022-02-01", "2022-11-30"
        );

        // Assert HTTP status
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
            "Cuando Jasper devuelve null, debe devolver 404");

        // Assert body is ResponseData
        ResponseData body = response.getBody();
        assertNotNull(body, "El cuerpo no debe ser null");

        // Comprueba el código interno (Constants.S_VAL_01)
        assertEquals(Constants.S_VAL_01, body.getCode(),
            "Debe usar el código de error de la constante");

        // Mensaje de error según buildResponseWithError
        assertEquals("Error al generar el reporte.", body.getMessage());

        // Y el detalle dentro del objeto exception
        ExceptionResponse ex = body.getException();
        assertNotNull(ex, "ExceptionResponse no debe ser null");
        assertEquals("Periodo: 2022, Opción: 1, Tipo de archivo: xlsx",
            ex.getDetails());

        // Verifica que se invocaron correctamente los mocks
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

    /**
     * Verifies that a non-numeric year parameter triggers NumberFormatException.
     *
     * @throws Exception for controller invocation
     */
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

    /**
     * Verifies that when the Jasper service throws a JRException,
     * the controller rethrows it.
     *
     * @throws Exception for controller invocation
     */
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