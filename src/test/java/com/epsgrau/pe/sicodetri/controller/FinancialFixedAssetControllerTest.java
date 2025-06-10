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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import com.epsgrau.pe.sicodetri.service.ReportJasperService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategy;
import com.epsgrau.pe.sicodetri.service.report.ReportStrategyFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class test for FinancialFixedAssetController. <br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Return success response when Excel file is uploaded and processed correctly")
    void returnSuccessResponseWhenExcelFileIsUploadedAndProcessedCorrectly() throws Exception {
        // Arrange
        String period = "2022-01-01";
        String fileName = "test.xlsx";
        ByteArrayInputStream bais = createMockExcelInputStream();

        MultipartFile file = new MockMultipartFile("file", fileName, TYPE_XLSX, bais);
        List<InputData> inputDataList = getListInputData();

        when(service.loadExcelFileToObjectService(file, period)).thenReturn(inputDataList);

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
        ByteArrayInputStream errorInputStream  = createMockExcelInputStreamFailed();
        MultipartFile file = new MockMultipartFile("file", fileName, TYPE_XLSX, errorInputStream);

        when(service.loadExcelFileToObjectService(file, period))
                .thenThrow(new RuntimeException("Error al procesar archivo: test.xlsx"));

        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
                .file((MockMultipartFile) file)
                .param("period", period);

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

    @Test
    @DisplayName("Return created response when input data is stored successfully from session")
    void returnCreatedResponseWhenInputDataIsStoredSuccessfullyFromSession() {
        // Arrange
        String period = "2022-01-01";
        List<InputData> inputDataList = getListInputData();
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(inputDataList);

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController(period, session);

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
        List<InputData> listInputData = Collections.singletonList(new InputData()); // Simula datos de entrada
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(listInputData);
        doThrow(new RuntimeException("Test Exception")).when(service).saveAllService(anyString(), anyList());

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController(period, session);

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
    @DisplayName("Return expectation failed when session input data is empty")
    void returnExpectationFailedWhenSessionInputDataIsEmpty() {
        // Arrange
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2024-08", session);

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información de entrada"));
    }

    @Test
    @DisplayName("Return OK when financial projection is generated successfully")
    void returnOkWhenFinancialProjectionIsGeneratedSuccessfully() {
        // Arrange
        String period = "2022-01-01";
        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString()))
                .thenReturn(getListFinancialFixedAsset());

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, S_VAL_332);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(S_VAL_00, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Proyección generada correctamente"));
    }

    @Test
    @DisplayName("Return expectation failed when no data is found for projection generation")
    void returnExpectationFailedWhenNoDataIsFoundForProjectionGeneration() {
        // Arrange
        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString())).thenReturn(new ArrayList<>());

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, "2024-08-01", "account");

        // Assert
        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al generar proyección"));
    }

    @Test
    @DisplayName("Return expectation failed when exception is thrown during projection generation")
    void returnExpectationFailedWhenExceptionIsThrownDuringProjectionGeneration() {
        // Arrange
        String period = "2024-08-01";
        String account = "account";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString()))
                .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ProjectionType projectionType = ProjectionType.TEMPORARY_ADDITIONS;
        ResponseEntity<ResponseData> response = controller.generateProjectionController(projectionType, period, account);

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

    @ParameterizedTest
    @CsvSource({
            "1, 2022-01-01, , , OK, 00, Se obtuvo informacion para el periodo",       // Caso éxito para N_VAL_1
            "1, 2022-02-01, , , NOT_FOUND, 01, Error al obtener informacion para el periodo", // Caso fallo para N_VAL_1
            "2, , 22.01.010862, , OK, 00, Se obtuvo informacion para el código de activo fijo principal", // Caso éxito para N_VAL_2
            "2, , 22.01.010773, , NOT_FOUND, 01, Error al obtener informacion para el código de activo fijo principal", // Caso fallo para N_VAL_2
            "3, , , 3321161001, OK, 00, Se obtuvo informacion para la cuenta contable", // Caso éxito para N_VAL_3
            "3, , , 3321161002, NOT_FOUND, 01, Error al obtener informacion para la cuenta contable", // Caso fallo para N_VAL_3
            "999, 2024-08-01, , , EXPECTATION_FAILED, 01, Error al obtener informacion del activo fijo" // Caso opción inválida
    })
    @DisplayName("Return appropriate response for getFinancialFixedAssetController based on option and input")
    void returnAppropriateResponseForGetFinancialFixedAssetControllerBasedOnOptionAndInput(int option, String period, String mainFixedAssetCode, String accountingAccount,
                                              HttpStatus expectedStatus, String expectedCode, String expectedMessage) {
        // Arrange
        List<FinancialFixedAsset> financialFixedAssets = getListFinancialFixedAsset();

        if (option == N_VAL_1) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByPeriodService(any(LocalDate.class))).thenReturn(financialFixedAssets);
            } else {
                when(service.findByPeriodService(any(LocalDate.class))).thenReturn(null);
            }
        } else if (option == N_VAL_2) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByMainFixedAssetCodeService(mainFixedAssetCode)).thenReturn(financialFixedAssets);
            } else {
                when(service.findByMainFixedAssetCodeService(mainFixedAssetCode)).thenReturn(null);
            }
        } else if (option == N_VAL_3) {
            if (expectedStatus == HttpStatus.OK) {
                when(service.findByFixedAssetAccountingAccountService(accountingAccount)).thenReturn(financialFixedAssets);
            } else {
                when(service.findByFixedAssetAccountingAccountService(accountingAccount)).thenReturn(null);
            }
        }

        // Act
        ResponseEntity<ResponseData> response = controller.getFinancialFixedAssetController(option, period, mainFixedAssetCode, accountingAccount);

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

        // Simulate the service to throw an exception
        when(service.findByPeriodService(any(LocalDate.class)))
                .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ResponseEntity<ResponseData> response = controller.getFinancialFixedAssetController(
                option, period, mainFixedAssetCode, accountingAccount);

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

    @Test
    @DisplayName("Throw ResourceNotFoundException when Jasper return null")
    void throwResourceNotFoundExceptionWhenJasperReturnNull() throws Exception {
        // Arrange
        String option = "1";

        // 1) Fábrica → estrategia mockeada
        when(reportStrategyFactory.getStrategy(option)).thenReturn(reportStrategy);

        // 2) Estrategia → lista NO vacía
        List<FinancialFixedAssetDto> reportList = List.of(new FinancialFixedAssetDto());
        when(reportStrategy.generateReport(any(ParamReportDto.class)))
            .thenReturn(reportList);

        // 3) **Muy importante**: indicamos qué template y fileName maneja la estrategia
        when(reportStrategy.getTemplate()).thenReturn("rpt_tax_book.jrxml");
        when(reportStrategy.getBaseFileName()).thenReturn("Libro_Tributario_Activo_Fijo_");

        // 4) Jasper devuelve null → forzamos el 404
        when(reportJasperService.getJasperReportInBytes(
            anyString(),//option
            anyString(),    // templateReport
            anyString(),    // fileType
            anyMap(),       // parameters
            anyList()       // dataSource
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
            // Aquí el GlobalExceptionHandler ya no envuelve en ResponseData con 'code',
            // sino que construye un JSON con timestamp, message y details:
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.message").value("Error al obtener reporte"))
            .andExpect(jsonPath("$.details").exists());

        // Verificaciones de invocaciones
        verify(reportStrategyFactory).getStrategy(option);
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        // Ahora sí coincide: primero argumento = "rpt_tax_book.jrxml"
        verify(reportJasperService).getJasperReportInBytes(
            eq(option),
            eq("rpt_tax_book.jrxml"),
            eq("PDF"),
            anyMap(),
            eq(reportList)
        );
    }

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

    @Test
    @DisplayName("Return OK when period exists and return NOT_FOUND when period has no data")
    void returnOkWhenPeriodExistsAndReturnNotFoundWhenPeriodHasNoData() {
        // Arrange: caso OK
        LocalDate fechaValida = LocalDate.now();
        when(service.findByPeriodService(any(LocalDate.class))).thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
                N_VAL_1,
                fechaValida.format(DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD)),
                "", "");

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para el periodo"));

        // Arrange: caso NOT_FOUND
        when(service.findByPeriodService(any(LocalDate.class))).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
                N_VAL_1,
                fechaValida.format(DateTimeFormatter.ofPattern(FORMATTER_YYYY_MM_DD)),
                "", "");

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para el periodo"));
    }

    @Test
    @DisplayName("Return OK when mainFixedAssetCode exists and return NOT_FOUND when none")
    void returnOkWhenMainFixedAssetCodeExistsAndReturnNotFoundWhenNone() {
        // Arrange: caso OK
        when(service.findByMainFixedAssetCodeService(anyString()))
                .thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
                N_VAL_2, "", "ABC123", "");

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para el código de activo fijo principal"));

        // Arrange: caso NOT_FOUND
        when(service.findByMainFixedAssetCodeService(anyString())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
                N_VAL_2, "", "ABC123", "");

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para el código de activo fijo principal"));
    }

    @Test
    @DisplayName("Return OK when accountingAccount exists and return NOT_FOUND when none")
    void returnOkWhenAccountingAccountExistsAndReturnNotFoundWhenNone() {
        // Arrange: caso OK
        when(service.findByFixedAssetAccountingAccountService(anyString()))
                .thenReturn(List.of(new FinancialFixedAsset()));

        // Act
        ResponseEntity<ResponseData> respOk = controller.getFinancialFixedAssetController(
                N_VAL_3, "", "", "ACC-001");

        // Assert OK
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals(S_VAL_00, respOk.getBody().getCode());
        assertTrue(respOk.getBody().getMessage().contains("Se obtuvo informacion para la cuenta contable"));

        // Arrange: caso NOT_FOUND
        when(service.findByFixedAssetAccountingAccountService(anyString()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<ResponseData> respNotFound = controller.getFinancialFixedAssetController(
                N_VAL_3, "", "", "ACC-001");

        // Assert NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, respNotFound.getStatusCode());
        assertEquals(S_VAL_01, respNotFound.getBody().getCode());
        assertTrue(respNotFound.getBody().getMessage().contains("Error al obtener informacion para la cuenta contable"));
    }

    @Test
    @DisplayName("Return EXPECTATION_FAILED when option is invalid")
    void returnExpectationFailedWhenOptionIsInvalid() {
        ResponseEntity<ResponseData> resp = controller.getFinancialFixedAssetController(
                999, "2022-01-01", "X", "Y"
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
            .andExpect(jsonPath("$.details").exists());

        verify(reportStrategyFactory).getStrategy(eq(option));
        verify(reportStrategy).generateReport(any(ParamReportDto.class));
        verifyNoMoreInteractions(reportJasperService);
    }
}