package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.ResponseData;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelperTest.*;
import static com.epsgrau.pe.sicodetri.util.Constants.LIST_INPUT_DATA;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FinancialFixedAssetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinancialFixedAssetService service;

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
    @DisplayName("uploadInputDataController_ShouldReturnSuccess")
    void uploadInputDataController_ShouldReturnSuccess() throws Exception {
        // Arrange
        ByteArrayInputStream bais = createMockExcelInputStream();
        MultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", bais);

        List<InputData> inputDataList = new ArrayList<>();
        InputData inputData = new InputData();
        inputData.setMainFixedAssetCode("21.02.010149");
        FixedAssetDetail inputDataDetail = new FixedAssetDetail();
        inputDataDetail.setDescription("TERRENO LAGUNA OXIDACION PAITA");
        inputData.setFixedAssetDetail(inputDataDetail);
        inputDataList.add(inputData);

        when(service.loadExcelFileToObject(file)).thenReturn(inputDataList);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/financial-fixed-asset/upload")
                        .file((MockMultipartFile) file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Archivo subio correctamente: test.xlsx"));
    }

    @Test
    @DisplayName("uploadInputDataController_ShouldReturnErrorFormatNotAllowed")
    void uploadInputDataController_ShouldReturnErrorFormatNotAllowed() throws Exception {
        // Arrange
        String errorMessage = "Error al cargar archivo: test.xls\nFormato permitido: xlsx";
        ResponseData expectedResponse = new ResponseData();
        expectedResponse.setMessage(errorMessage);
        expectedResponse.setException(new ExceptionResponse(LocalDateTime.now(), errorMessage, ""));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.xls");
        when(file.getContentType()).thenReturn("application/vnd.ms-excel");
        when(file.getBytes()).thenThrow(new RuntimeException("Exception message"));
        doThrow(new RuntimeException("Exception message")).when(service).loadExcelFileToObject(any(MultipartFile.class));

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/financial-fixed-asset/upload")
                        .file(new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", new byte[0]))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("saveFinancialFixedAssetController_ShouldReturnSuccess")
    void saveFinancialFixedAssetController_ShouldReturnSuccess() throws Exception {
        // Arrange
        String period = "2024-01-01";
        MockHttpSession session = new MockHttpSession();
        List<InputData> inputDataList = getListInputData();
        session.setAttribute(LIST_INPUT_DATA, inputDataList);

        // Act & Assert
        mockMvc.perform(post("/api/v1/financial-fixed-asset")
                        .param("period", period)
                        .session(session))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Informacion guardada de manera correcta para el periodo " + period));
    }

    @Test
    @DisplayName("saveFinancialFixedAssetController_ShouldReturnErrorOnException")
    void saveFinancialFixedAssetController_ShouldReturnErrorOnException() throws Exception {
        // Arrange
        String period = "2024-01-01";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(LIST_INPUT_DATA, null); // Simular atributo nulo

        doThrow(new RuntimeException("Exception message")).when(service).saveAllService(any(LocalDate.class), anyList());

        // Act & Assert
        mockMvc.perform(post("/api/v1/financial-fixed-asset")
                        .param("period", period)
                        .session(session))
                .andExpect(status().isExpectationFailed())
                .andExpect(jsonPath("$.message").value("Error al guardar informacion para el periodo " + period));
    }

    @Test
    @DisplayName("getAllFinancialFixedAssetsController_ShouldReturnFinancialFixedAssets")
    void getAllFinancialFixedAssetsController_ShouldReturnFinancialFixedAssets() throws Exception {
        // Arrange
        ResponseData expectedResponse = new ResponseData();
        String period = "2024-01-01";
        List<FinancialFixedAsset> financialFixedAssets = getListFinancialFixedAsset();
        FinancialFixedAsset asset =financialFixedAssets.get(0);
        expectedResponse.setResponse(financialFixedAssets);

        when(service.findByPeriodService(any(LocalDate.class))).thenReturn(financialFixedAssets);

        // Act & Assert
        mockMvc.perform(get("/api/v1/financial-fixed-asset")
                        .param("period", period))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response[0].mainFixedAssetCode").value(asset.getMainFixedAssetCode()))
                .andExpect(jsonPath("$.response[0].description").value(asset.getDescription()))
        ;
    }

}