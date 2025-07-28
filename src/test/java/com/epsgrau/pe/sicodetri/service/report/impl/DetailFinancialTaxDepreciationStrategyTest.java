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

/**
 * Unit tests for the {@link DetailFinancialTaxDepreciationStrategy} class.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code getCode()} returns the correct strategy code.</li>
 *   <li>{@code getTemplate()} returns the correct JRXML template name.</li>
 *   <li>{@code getBaseFileName()} returns the proper base filename prefix.</li>
 *   <li>{@code generateReport(ParamReportDto)} delegates to
 *       {@link FinancialFixedAssetService#callGetReportDetailFinancialAndTaxDepreciationService}
 *       and correctly handles returned data, empty lists, and exceptions.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link DetailFinancialTaxDepreciationStrategyTest}<br/>
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
 *     <li>Jun 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("DetailFinancialTaxDepreciationStrategy Tests")
class DetailFinancialTaxDepreciationStrategyTest {

  @Mock
  private FinancialFixedAssetService service;

  @InjectMocks
  private DetailFinancialTaxDepreciationStrategy strategy;

  /**
   * Initializes Mockito annotations before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Verifies that {@link DetailFinancialTaxDepreciationStrategy#getCode()}
   * returns the expected strategy code "2".
   */
  @Test
  @DisplayName("Return code when getCode is called")
  void returnCodeWhenGetCodeIsCalled() {
    // Act
    String code = strategy.getCode();

    // Assert
    assertEquals("2", code, "Debe devolver '2' como código de estrategia");
  }

  /**
   * Verifies that {@link DetailFinancialTaxDepreciationStrategy#getTemplate()}
   * returns the correct JRXML template name.
   */
  @Test
  @DisplayName("Return template when getTemplate is called")
  void returnTemplateWhenGetTemplateIsCalled() {
    // Act
    String template = strategy.getTemplate();

    // Assert
    assertEquals("rpt_detail_financial_tax_depreciation.jrxml", template,
        "Debe devolver el nombre correcto de la plantilla");
  }

  /**
   * Verifies that {@link DetailFinancialTaxDepreciationStrategy#getBaseFileName()}
   * returns the expected base filename prefix.
   */
  @Test
  @DisplayName("Return base file name when getBaseFileName is called")
  void returnBaseFileNameWhenGetBaseFileNameIsCalled() {
    // Act
    String baseName = strategy.getBaseFileName();

    // Assert
    assertEquals("Libro_Detalle_Financiero_Tributario_Depreciacion_", baseName,
        "Debe devolver el prefijo de archivo correcto");
  }

  /**
   * Verifies that {@link DetailFinancialTaxDepreciationStrategy#generateReport(ParamReportDto)}
   * delegates to {@link FinancialFixedAssetService#callGetReportDetailFinancialAndTaxDepreciationService}
   * and returns the same list when the service provides data.
   */
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

  /**
   * Verifies that {@code generateReport} returns a non-null but empty list
   * when the underlying service returns an empty list.
   */
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

  /**
   * Verifies that {@code generateReport} propagates a {@link RuntimeException}
   * when the underlying service throws one.
   */
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