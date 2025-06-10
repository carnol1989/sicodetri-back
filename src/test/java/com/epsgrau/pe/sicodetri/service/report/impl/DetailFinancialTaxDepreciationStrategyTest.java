package com.epsgrau.pe.sicodetri.service.report.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.dto.FinancialFixedAssetDto;
import com.epsgrau.pe.sicodetri.dto.ParamReportDto;
import com.epsgrau.pe.sicodetri.service.FinancialFixedAssetService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("DetailFinancialTaxDepreciationStrategy Tests")
class DetailFinancialTaxDepreciationStrategyTest {

  @Mock
  private FinancialFixedAssetService service;

  @InjectMocks
  private DetailFinancialTaxDepreciationStrategy strategy;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Return code when getCode is called")
  void returnCodeWhenGetCodeIsCalled() {
    // Act
    String code = strategy.getCode();

    // Assert
    assertEquals("2", code, "Debe devolver '2' como código de estrategia");
  }

  @Test
  @DisplayName("Return template when getTemplate is called")
  void returnTemplateWhenGetTemplateIsCalled() {
    // Act
    String template = strategy.getTemplate();

    // Assert
    assertEquals("rpt_detail_financial_tax_depreciation.jrxml", template,
        "Debe devolver el nombre correcto de la plantilla");
  }

  @Test
  @DisplayName("Return base file name when getBaseFileName is called")
  void returnBaseFileNameWhenGetBaseFileNameIsCalled() {
    // Act
    String baseName = strategy.getBaseFileName();

    // Assert
    assertEquals("Libro_Detalle_Financiero_Tributario_Depreciacion_", baseName,
        "Debe devolver el prefijo de archivo correcto");
  }

  @Test
  @DisplayName("Generate report when service returns data")
  void processReportWhenServiceReturnsData() {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    List<FinancialFixedAssetDto> dtoList = Arrays.asList(
        new FinancialFixedAssetDto(), new FinancialFixedAssetDto()
    );
    when(service.callGetReportDetailFinancialAndTaxDepreciationService(params))
        .thenReturn(dtoList);

    // Act
    List<FinancialFixedAssetDto> result = strategy.generateReport(params);

    // Assert
    assertSame(dtoList, result, "Debe devolver exactamente la lista que retorna el servicio");
    verify(service, times(1)).callGetReportDetailFinancialAndTaxDepreciationService(params);
  }

  @Test
  @DisplayName("Generate empty report when service returns empty list")
  void generateEmptyReportWhenServiceReturnsEmptyList() {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    when(service.callGetReportDetailFinancialAndTaxDepreciationService(params))
        .thenReturn(Collections.emptyList());

    // Act
    List<FinancialFixedAssetDto> result = strategy.generateReport(params);

    // Assert
    assertNotNull(result, "La lista no debe ser null");
    assertTrue(result.isEmpty(), "La lista debe estar vacía cuando el servicio no retorna datos");
  }

  @Test
  @DisplayName("Propagate runtime exception when service fails")
  void propagateRuntimeExceptionWhenServiceFails() {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    when(service.callGetReportDetailFinancialAndTaxDepreciationService(params))
        .thenThrow(new RuntimeException("Service failure"));

    // Act & Assert
    RuntimeException ex = assertThrows(RuntimeException.class, () -> strategy.generateReport(params),
        "Si el servicio lanza RuntimeException, debe propagarse");
    assertEquals("Service failure", ex.getMessage());
  }

}