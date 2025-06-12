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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("TaxBookReportStrategy Tests")
class TaxBookReportStrategyTest {

  @Mock
  private FinancialFixedAssetService service;

  @InjectMocks
  private TaxBookReportStrategy strategy;

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
    assertEquals("1", code, "Debe devolver '1' como código de estrategia");
  }

  @Test
  @DisplayName("Return template when getTemplate is called")
  void returnTemplateWhenGetTemplateIsCalled() {
    // Act
    String template = strategy.getTemplate();

    // Assert
    assertEquals("rpt_tax_book.jrxml", template,
        "Debe devolver 'rpt_tax_book.jrxml' como nombre de plantilla");
  }

  @Test
  @DisplayName("Return base file name when getBaseFileName is called")
  void returnBaseFileNameWhenGetBaseFileNameIsCalled() {
    // Act
    String baseName = strategy.getBaseFileName();

    // Assert
    assertEquals("Libro_Tributario_Activo_Fijo_", baseName,
        "Debe devolver 'Libro_Tributario_Activo_Fijo_' como prefijo de nombre de archivo");
  }

  @Test
  @DisplayName("Generate report when service returns data")
  void processReportWhenServiceReturnsData() throws SQLException {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    List<FinancialFixedAssetDto> dtoList = Arrays.asList(
        new FinancialFixedAssetDto(), new FinancialFixedAssetDto()
    );
    when(service.callGetReportTaxBookService(params)).thenReturn(dtoList);

    // Act
    List<FinancialFixedAssetDto> result = strategy.generateReport(params);

    // Assert
    assertSame(dtoList, result, "Debe devolver la lista que retorna el servicio");
    verify(service, times(1)).callGetReportTaxBookService(params);
  }

  @Test
  @DisplayName("Throw SQLException when service fails")
  void throwSQLExceptionWhenServiceFails() throws SQLException {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    when(service.callGetReportTaxBookService(params))
        .thenThrow(new SQLException("DB error"));

    // Act & Assert
    SQLException ex = assertThrows(SQLException.class, () -> strategy.generateReport(params),
        "Si el servicio falla con SQLException debe propagarse");
    assertEquals("DB error", ex.getMessage());
  }

  @Test
  @DisplayName("Generate empty report when service returns empty list")
  void generateEmptyReportWhenServiceReturnsEmptyList() throws SQLException {
    // Arrange
    ParamReportDto params = new ParamReportDto();
    when(service.callGetReportTaxBookService(params))
        .thenReturn(Collections.emptyList());

    // Act
    List<FinancialFixedAssetDto> result = strategy.generateReport(params);

    // Assert
    assertNotNull(result, "La lista no debe ser null");
    assertTrue(result.isEmpty(), "La lista debe estar vacía cuando el servicio no retorna datos");
  }

}