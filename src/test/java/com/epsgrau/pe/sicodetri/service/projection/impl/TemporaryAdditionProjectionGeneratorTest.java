package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialFixedAsset332;
import static com.epsgrau.pe.sicodetri.util.DataTest.getTaxTable332;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atLeastOnce;
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

@DisplayName("TemporaryAdditionProjectionGenerator Tests")
class TemporaryAdditionProjectionGeneratorTest {

  @Mock
  private TaxTableSunatService taxTableService;
  @Mock
  private FinancialDepreciationService financialDepreciationService;
  @Mock
  private TaxDepreciationService taxDepreciationService;
  @Mock
  private AdditionService additionService;
  @Mock
  private DeductionService deductionService;

  @InjectMocks
  private TemporaryAdditionProjectionGenerator generator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Return TEMPORARY_ADDITIONS when getType is called")
  void returnTemporaryAdditionsWhenGetTypeCalled() {
    assertEquals(ProjectionType.TEMPORARY_ADDITIONS, generator.getType());
  }

  @Test
  @DisplayName("generate skips all when tax table is null")
  void generateSkipsWhenTaxTableNull() {
    // Arrange
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "XYZ");
    // taxTableService returns null
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: no services should be invoked
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate skips non-eligible assets")
  void generateSkipsNonEligible() {
    // Arrange: tax table present
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());
    // Asset not eligible: acquisitionDate == LOCAL_DATE_DEFAULT
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setAcquisitionDate(LOCAL_DATE_DEFAULT);
    asset.setInitialBalance(BigDecimal.ZERO);
    asset.setRevaluedAmount(BigDecimal.ZERO);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: still no interactions
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate invokes services on eligible asset")
  void generateInvokesServicesWhenEligible() {
    // Arrange: tax table with usefulLifeYears = 2
    TaxTableSunat table = getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(table);

    FinancialFixedAsset asset = getFinancialFixedAsset332();

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: each projection year produces lists to persist
    verify(financialDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(taxDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(deductionService, atLeastOnce()).saveAllService(anyList());
    verify(additionService, atLeastOnce()).saveAllService(anyList());
  }

  @Test
  @DisplayName("generate skips assets with banned description")
  void generateSkipsWhenDescriptionContainsBanned() {
    // Arrange: tax table present
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setInitialBalance(new BigDecimal("100"));
    asset.setRevaluedAmount(BigDecimal.ZERO);
    asset.setWellAppraisedPreInvestmentStudyDry("Valor Tasado premium"); // banned

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: skip all
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }
}