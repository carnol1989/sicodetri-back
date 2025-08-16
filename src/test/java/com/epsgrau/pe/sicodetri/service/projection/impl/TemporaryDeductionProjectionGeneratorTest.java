package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import com.epsgrau.pe.sicodetri.util.DataTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link TemporaryDeductionProjectionGenerator}.
 *
 * <p>Verifies that this generator:
 * <ul>
 *   <li>Reports its correct projection type.</li>
 *   <li>Skips processing when the tax table is missing.</li>
 *   <li>Skips assets that fail eligibility criteria:
 *     default acquisition date, zero initial balance, non-zero revalued amount, incorrect prefixes.</li>
 *   <li>Processes eligible assets and invokes all persistence services exactly once.</li>
 *   <li>Handles empty asset lists without error.</li>
 *   <li>Handles multi-year projections correctly.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link TemporaryDeductionProjectionGeneratorTest}<br/>
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
@DisplayName("TemporaryDeductionProjectionGenerator Tests")
class TemporaryDeductionProjectionGeneratorTest {

  @Mock
  private TaxTableSunatService taxTableService;

  @Mock
  private FinancialDepreciationService financialDepreciationService;

  @Mock
  private TaxDepreciationService taxDepreciationService;

  @Mock
  private DeductionService deductionService;

  @Mock
  private AdditionService additionService;

  @InjectMocks
  private TemporaryDeductionProjectionGenerator generator;

  /**
   * Initializes Mockito annotations before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Verifies that {@code getType()} returns TEMPORARY_DEDUCTIONS.
   */
  @Test
  @DisplayName("Return TEMPORARY_DEDUCTIONS when getType is called")
  void returnProjectionTypeWhenGetTypeCalled() {
    // Act
    ProjectionType type = generator.getType();

    // Assert
    assertEquals(ProjectionType.TEMPORARY_DEDUCTIONS, type,
        "getType debe devolver TEMPORARY_DEDUCTIONS");
  }

  /**
   * Ensures that no processing occurs when the tax table service returns null.
   */
  @Test
  @DisplayName("generate does nothing when tax table is null")
  void generateSkipsWhenTaxTableIsNull() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LocalDate.of(2020,1,1));
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "ABC");
    asset.setInitialBalance(BigDecimal.TEN);
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setUsefulLifeYears(1.0);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: no service interactions
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Verifies that assets with default acquisition date, zero balance, or non-zero revalued
   * amount are skipped.
   */
  @Test
  @DisplayName("generate skips non-eligible assets")
  void generateSkipsNonEligible() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());
    // asset with default date ⇒ not eligible
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LOCAL_DATE_DEFAULT);
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "XYZ");
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setUsefulLifeYears(1.0);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: still no interactions
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Ensures that eligible assets trigger all four persistence service calls exactly once.
   */
  @Test
  @DisplayName("generate processes eligible assets and invokes all services")
  void generateProcessesEligible() {
    // Arrange
    TaxTableSunat table = new TaxTableSunat();
    table.setUsefulLifeYears(1);
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LocalDate.of(2020,1,1));
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setInitialBalance(BigDecimal.valueOf(100));
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setUsefulLifeYears(1.0);

    LocalDate period = LocalDate.of(2022,1,1);

    // Act
    generator.generate(period, List.of(asset));

    // Assert: each service should be invoked once
    verify(financialDepreciationService, times(1)).saveAllService(anyList());
    verify(taxDepreciationService, times(1)).saveAllService(anyList());
    verify(deductionService, times(1)).saveAllService(anyList());
    verify(additionService, times(1)).saveAllService(anyList());
  }

  /**
   * Verifies that assets with prefixes outside the eligible range are skipped.
   */
  @Test
  @DisplayName("generate skips assets with wrong prefix")
  void generateSkipsWhenPrefixNotEligible() {
    // Arrange
    TaxTableSunat table = new TaxTableSunat();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LocalDate.of(2020, 1, 1));
    asset.setFixedAssetAccountingAccount("3339999999"); // no empieza con 332
    asset.setInitialBalance(BigDecimal.TEN);
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setUsefulLifeYears(1.0);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Verifies that assets with a zero initial balance are skipped,
   * i.e., no projection services are invoked when initial balance is zero.
   */
  @Test
  @DisplayName("generate skips assets with zero initial balance")
  void generateSkipsWhenInitialBalanceZero() {
    // Arrange
    TaxTableSunat table = new TaxTableSunat();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LocalDate.of(2020, 1, 1));
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setInitialBalance(BigDecimal.ZERO); // saldo cero
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setUsefulLifeYears(1.0);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Verifies that assets with a non-zero revalued amount are skipped,
   * i.e., no projection services are invoked when revalued amount is greater than zero.
   */
  @Test
  @DisplayName("generate skips assets with non-zero revalued amount")
  void generateSkipsWhenRevaluedAmountNonZero() {
    // Arrange
    TaxTableSunat table = new TaxTableSunat();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setAcquisitionDate(LocalDate.of(2020, 1, 1));
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE); // revalorizado distinto de cero
    asset.setUsefulLifeYears(1.0);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Ensures that empty asset lists are handled gracefully with no service interactions.
   */
  @Test
  @DisplayName("generate does nothing when no assets provided")
  void generateDoesNothingWhenNoAssets() {
    // Arrange
    // tabla existe
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(new TaxTableSunat());

    // Act
    generator.generate(LocalDate.now(), Collections.emptyList());

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, deductionService, additionService);
  }

  /**
   * Verifies correct multi-year projection behavior: financial depreciation list is non-empty
   * and each persistence service is invoked once.
   */
  @Test
  @DisplayName("generate processes multiple years correctly")
  void generateProcessesMultipleYears() {
    // Arrange
    TaxTableSunat table = DataTest.getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    LocalDate period      = LocalDate.of(2022, 1, 1);
    // yearsPassed = 2, remaining = 3 - 2 = 1 → 1 iteración
    FinancialFixedAsset asset = DataTest.getFinancialFixedAsset332();

    // Act
    generator.generate(period, List.of(asset));

    // Assert: capturamos la lista que se guarda para asegurarnos de que fue poblada
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<FinancialDepreciation>> captor =
        (ArgumentCaptor<List<FinancialDepreciation>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);

    verify(financialDepreciationService, times(1)).saveAllService(captor.capture());
    List<?> fedList = captor.getValue();
    assertFalse(fedList.isEmpty(), "Debe haber al menos una depreciación financiera tras una iteración");

    verify(taxDepreciationService, times(1)).saveAllService(anyList());
    verify(deductionService, times(1)).saveAllService(anyList());
    verify(additionService, times(1)).saveAllService(anyList());
  }
}