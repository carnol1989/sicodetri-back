package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.*;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.*;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListFinancialFixedAsset;
import static com.epsgrau.pe.sicodetri.util.DataTest.getListInputData;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
class FinancialFixedAssetControllerTest {

    public static String BASE_URI = "/api/v1/financial-fixed-assets";

    private MockMvc mockMvc;

    @Mock
    private FinancialFixedAssetService service;

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
    @DisplayName("testUploadInputDataController_Success")
    void testUploadInputDataController_Success() throws Exception {
        // Arrange
        ByteArrayInputStream bais = createMockExcelInputStream();
        MultipartFile file = new MockMultipartFile("file", "test.xlsx",
                TYPE_XLSX, bais);
        List<InputData> inputDataList = getListInputData();

        when(service.loadExcelFileToObjectService(file, "2022-01-01")).thenReturn(inputDataList);

        // Crear una sesión simulada
        MockHttpSession session = new MockHttpSession();

        // Establecer la sesión en el request simulado
        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
                .file((MockMultipartFile) file)
                .param("period", "2022-01-01")
                .session(session);

        // Act
        MvcResult result = mockMvc.perform(requestBuilder)
                .andReturn(); // Capturar el resultado

        // Obtener el contenido de la respuesta
        String responseContent = result.getResponse().getContentAsString();

        // Validar el contenido de la respuesta usando jsonPath
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
    @DisplayName("testUploadInputDataController_FailureInvalidFormat")
    void testUploadInputDataController_FailureInvalidFormat() throws Exception {
        // Arrange
        // Crear un MockMultipartFile con un formato inválido
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new byte[0]);

        // Simular el comportamiento esperado del servicio cuando se pasa un archivo con un formato no válido
        when(service.loadExcelFileToObjectService(file, "2022-01-01")).thenThrow(new RuntimeException("Error de formato"));

        // Simular el request y llamar al método del controlador
        MvcResult result = mockMvc.perform(multipart(BASE_URI + "/upload")
                        .file(file)  // Usa el MockMultipartFile directamente
                        .param("period", "2022-01-01"))
                .andReturn();

        // Obtener el contenido de la respuesta
        String responseContent = result.getResponse().getContentAsString();

        // Validar la respuesta
        assertEquals(HttpStatus.EXPECTATION_FAILED.value(), result.getResponse().getStatus());
        assertTrue(responseContent.contains("Formato no permitido. Debe ser"));
    }

    @Test
    @DisplayName("testUploadInputDataController_FailureException")
    void testUploadInputDataController_FailureException() throws Exception {
        // Arrange
        ByteArrayInputStream bais = createMockExcelInputStreamFailed(); // Generar un flujo que simule un error
        MultipartFile file = new MockMultipartFile("file", "test.xlsx", TYPE_XLSX, bais);

        // Configurar el comportamiento del servicio para lanzar una excepción
        when(service.loadExcelFileToObjectService(file, "2022-01-01")).thenThrow(new RuntimeException("Error al procesar archivo: test.xlsx"));

        // Establecer la sesión en el request simulado
        MockHttpServletRequestBuilder requestBuilder = multipart(BASE_URI + "/upload")
                .file((MockMultipartFile) file)  // Usa el MockMultipartFile directamente
                .param("period", "2022-01-01");

        // Act
        MvcResult result = mockMvc.perform(requestBuilder)
                .andReturn(); // Capturar el resultado

        // Obtener el contenido de la respuesta
        String responseContent = result.getResponse().getContentAsString();

        // Validar la respuesta
        mockMvc.perform(requestBuilder)
                .andExpect(status().isExpectationFailed())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(S_VAL_01))
                .andExpect(jsonPath("$.message").value("Error al subir archivo."))
                .andExpect(jsonPath("$.timestamp").exists()) // Validar que timestamp existe
                .andExpect(jsonPath("$.exception").exists()) // Validar que `exception` existe
                .andExpect(jsonPath("$.exception.timestamp").exists()) // Validar campo timestamp del objeto `ExceptionResponse`
                .andExpect(jsonPath("$.exception.message").exists()) // Validar campo message del objeto `ExceptionResponse`
                .andExpect(jsonPath("$.exception.details").exists()); // Validar campo details del objeto `ExceptionResponse`
    }

    @Test
    @DisplayName("testSaveFinancialFixedAssetController_Success")
    void testSaveFinancialFixedAssetController_Success() {
        List<InputData> inputDataList = getListInputData();
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(inputDataList);

        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2022-01-01", session);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(S_VAL_00, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Información guardada correctamente"));
    }

    @Test
    @DisplayName("testSaveFinancialFixedAssetController_ExceptionHandling")
    void testSaveFinancialFixedAssetController_ExceptionHandling() {
        // Arrange
        String period = "2024-08-01";
        List<InputData> listInputData = Collections.singletonList(new InputData()); // Simula datos de entrada

        // Configura el mock del session para devolver una lista de InputData
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(listInputData);

        // Configura el mock del servicio para lanzar una excepción
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
    @DisplayName("testSaveFinancialFixedAssetController_Failure")
    void testSaveFinancialFixedAssetController_Failure() {
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(null);

        ResponseEntity<ResponseData> response = controller.saveFinancialFixedAssetController("2024-08", session);

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al obtener información de entrada"));
    }

    @Test
    @DisplayName("testGenerateProjectionController_Success")
    void testGenerateProjectionController_Success() {
        String period = "2022-01-01";

        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString())).thenReturn(getListFinancialFixedAsset());

        ResponseEntity<ResponseData> response = controller.generateProjectionController(period, S_VAL_332);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(S_VAL_00, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Proyección generada correctamente"));
    }

    @Test
    @DisplayName("testGenerateProjectionController_Failure")
    void testGenerateProjectionController_Failure() {
        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString())).thenReturn(new ArrayList<>());

        ResponseEntity<ResponseData> response = controller.generateProjectionController("2024-08-01", "account");

        assertEquals(HttpStatus.EXPECTATION_FAILED, response.getStatusCode());
        assertEquals(S_VAL_01, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains("Error al generar proyección"));
    }

    @Test
    @DisplayName("testGenerateProjectionController_ExceptionHandling")
    void testGenerateProjectionController_ExceptionHandling() {
        // Arrange
        String period = "2024-08-01";
        String account = "account";
        HttpServletRequest request = mock(HttpServletRequest.class);

        // Simulate the service to throw an exception
        when(service.findByPeriodAndAccountStartingWithService(any(LocalDate.class), anyString()))
                .thenThrow(new RuntimeException("Simulated exception"));

        // Act
        ResponseEntity<ResponseData> response = controller.generateProjectionController(period, account);

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
    @DisplayName("testGetFinancialFixedAssetController")
    void testGetFinancialFixedAssetController(int option, String period, String mainFixedAssetCode, String accountingAccount,
                                              HttpStatus expectedStatus, String expectedCode, String expectedMessage) {
        List<FinancialFixedAsset> financialFixedAssets = getListFinancialFixedAsset();

        // Configuración del mock según el caso
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

        // Ejecución del método
        ResponseEntity<ResponseData> response = controller.getFinancialFixedAssetController(option, period, mainFixedAssetCode, accountingAccount);

        // Verificaciones
        assertEquals(expectedStatus, response.getStatusCode());
        assertEquals(expectedCode, response.getBody().getCode());
        assertTrue(response.getBody().getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("testGetFinancialFixedAssetController_ExceptionHandling")
    void testGetFinancialFixedAssetController_ExceptionHandling() {
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

}