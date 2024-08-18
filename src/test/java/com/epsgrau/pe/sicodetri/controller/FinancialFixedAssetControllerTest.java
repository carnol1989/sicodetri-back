package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.model.FixedAssetDetail;
import com.epsgrau.pe.sicodetri.model.InputData;
import com.epsgrau.pe.sicodetri.model.DataResponse;
import com.epsgrau.pe.sicodetri.service.FinacialFixedAssetRegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epsgrau.pe.sicodetri.helper.ExcelHelper.hasExcelFormat;
import static com.epsgrau.pe.sicodetri.helper.ExcelHelperTest.createMockExcelInputStream;
import static com.epsgrau.pe.sicodetri.util.Constants.LIST_INPUT_DATA;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FinancialFixedAssetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinacialFixedAssetRegisterService service;

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
                        .file((MockMultipartFile) file))  // Use the MultipartFile directly
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    System.out.println("Response Content: " + content);  // Print response content for debugging
                    assertTrue(content.contains("Archivo subio correctamente"), "Archivo subio correctamente: ");
                });
    }

    @Test
    @DisplayName("uploadInputDataController_ShouldReturnErrorFormatNotAllowed")
    void uploadInputDataController_ShouldReturnErrorFormatNotAllowed() throws Exception {
        // Arrange
        String errorMessage = "Error al cargar archivo: test.xls\nFormato permitido: xlsx";
        DataResponse expectedResponse = new DataResponse();
        expectedResponse.setMessage(errorMessage);
        expectedResponse.setResponse(new ExceptionResponse(LocalDateTime.now(), errorMessage, ""));

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.xls");
        when(file.getContentType()).thenReturn("application/vnd.ms-excel");
        when(file.getBytes()).thenThrow(new RuntimeException("Exception message"));
        doThrow(new RuntimeException("Exception message")).when(service).loadExcelFileToObject(any(MultipartFile.class));

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/financial-fixed-asset/upload")
                        .file(new MockMultipartFile("file", "test.xls", "application/vnd.ms-excel", new byte[0]))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.response").isMap());
    }

    @Test
    @DisplayName("getAllTaxesController_ShouldReturnInputDataList")
    void getAllTaxesController_ShouldReturnInputDataList() throws Exception {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        List<InputData> inputDataList = new ArrayList<>();
        InputData inputData = new InputData();
        inputDataList.add(inputData);
        when(session.getAttribute(LIST_INPUT_DATA)).thenReturn(inputDataList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/financial-fixed-asset")
                        .sessionAttr(LIST_INPUT_DATA, inputDataList))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$").isArray());
    }

}