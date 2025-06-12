package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.service.AdditionService;
import com.epsgrau.pe.sicodetri.service.DeductionService;
import com.epsgrau.pe.sicodetri.service.FinancialDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxDepreciationService;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import com.epsgrau.pe.sicodetri.service.projection.ProjectionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Return TEMPORARY_DEDUCTIONS when getType is called")
  void returnProjectionTypeWhenGetTypeCalled() {
    // Act
    ProjectionType type = generator.getType();

    // Assert
    assertEquals(ProjectionType.TEMPORARY_DEDUCTIONS, type,
        "getType debe devolver TEMPORARY_DEDUCTIONS");
  }

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
}