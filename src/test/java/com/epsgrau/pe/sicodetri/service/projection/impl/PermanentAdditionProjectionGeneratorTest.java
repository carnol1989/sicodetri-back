package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
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
import com.epsgrau.pe.sicodetri.util.DataTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("PermanentAdditionProjectionGenerator Tests")
class PermanentAdditionProjectionGeneratorTest {

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
  private PermanentAdditionProjectionGenerator generator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("getType returns PERMANENT_ADDITIONS")
  void getTypeReturnsPermanentAdditions() {
    assertEquals(ProjectionType.PERMANENT_ADDITIONS, generator.getType());
  }

  @Test
  @DisplayName("generate skips when TaxTableSunatService returns null")
  void generateSkipsWhenTaxTableNull() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "001");

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate skips non-eligible assets")
  void generateSkipsNonEligibleAssets() {
    // Arrange: present table
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setAcquisitionDate(LOCAL_DATE_DEFAULT);      // fails acquisitionDate check
    asset.setWellAppraisedPreInvestmentStudyDry("tasacion");
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ONE);

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: skip because acquisitionDate == default
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate invokes persistence on eligible asset")
  void generateInvokesPersistenceOnEligibleAsset() {
    // Arrange: table with useful life
    TaxTableSunat table = DataTest.getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = DataTest.getFinancialFixedAsset332ForPermanentAddition();

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert: services called to persist projection lists
    verify(financialDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(taxDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(deductionService, atLeastOnce()).saveAllService(anyList());
    verify(additionService, atLeastOnce()).saveAllService(anyList());
  }

  @Test
  @DisplayName("generate does nothing when fixedAssets list is empty")
  void generateDoesNothingWhenNoAssets() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    // Act
    generator.generate(LocalDate.now(), Collections.emptyList());

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }

  @Test
  @DisplayName("generate skips asset when description is null")
  void generateSkipsWhenDescriptionNull() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "001");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setWellAppraisedPreInvestmentStudyDry(null);  // descripción nula

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }

  @Test
  @DisplayName("generate skips asset when description lacks keyword")
  void generateSkipsWhenDescriptionWrong() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "002");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setWellAppraisedPreInvestmentStudyDry("sin término relevante");  // no contiene "tasacion"

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }

  @Test
  @DisplayName("generate skips asset when historicalCost is zero")
  void generateSkipsWhenHistoricalCostZero() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "003");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ZERO);          // falla aquí
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setWellAppraisedPreInvestmentStudyDry("tasacion válida");

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }

  @Test
  @DisplayName("generate skips asset when revaluedAmount is zero")
  void generateSkipsWhenRevaluedAmountZero() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "004");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ZERO);          // falla aquí
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setWellAppraisedPreInvestmentStudyDry("valor tasado");

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }

  @Test
  @DisplayName("generate skips asset when initialBalance is zero")
  void generateSkipsWhenInitialBalanceZero() {
    // Arrange
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "005");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ZERO);         // falla aquí
    asset.setWellAppraisedPreInvestmentStudyDry("valor tasado");

    // Act
    generator.generate(LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }
}