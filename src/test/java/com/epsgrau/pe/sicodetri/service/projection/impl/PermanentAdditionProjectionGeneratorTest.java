package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
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
import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
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

/**
 * Unit test suite for {@link PermanentAdditionProjectionGenerator}.
 *
 * <p><strong>Scope</strong></p>
 * <ul>
 *   <li>Declares the generator type as {@link ProjectionType#PERMANENT_ADDITIONS}.</li>
 *   <li>Verifies short-circuit behavior when SUNAT tax table lookup is absent.</li>
 *   <li>Verifies eligibility gating (date/account/description/amounts) prevents persistence calls.</li>
 *   <li>Verifies the happy path triggers find → delete → save across all projection artifacts
 *       (financial/tax depreciation, additions, deductions).</li>
 *   <li>Ensures empty inputs are handled gracefully (no service interaction).</li>
 * </ul>
 *
 * <p><strong>Test doubles</strong></p>
 * <ul>
 *   <li>{@link TaxTableSunatService}, {@link FinancialDepreciationService},
 *       {@link TaxDepreciationService}, {@link AdditionService}, {@link DeductionService} are mocked.</li>
 *   <li>Concrete fixture builders come from {@link DataTest} for canonical assets/tax tables.</li>
 * </ul>
 *
 * <p><strong>Operational notes</strong></p>
 * <ul>
 *   <li>Interactions are asserted rather than content equality; the generator performs destructive
 *       updates (delete-then-save), which is validated via Mockito verifications.</li>
 *   <li>Eligibility is intentionally re-tested across multiple negative cases to lock behavior.</li>
 * </ul>
 *
 * <p><strong>Class</strong>: {@code PermanentAdditionProjectionGeneratorTest}<br/>
 * <strong>Company</strong>: EPS Grau S.A.<br/>
 * <strong>Copyright</strong>: &copy; 2025 EPS Grau S.A. All rights reserved.</p>
 *
 * <p><strong>Authors</strong>:</p>
 * <ul>
 *   <li>Carlos Augusto Nole Machaca</li>
 * </ul>
 *
 * <p><strong>Change log</strong>:</p>
 * <ul>
 *   <li>Jun 25, 2025 — Initial version.</li>
 * </ul>
 *
 * @since 1.0
 * @see PermanentAdditionProjectionGenerator
 * @see DataTest
 */
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
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "001");

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate skips non-eligible assets")
  void generateSkipsNonEligibleAssets() {
    // Arrange: present table
    String username = "katherine_gutierrez";
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
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: skip because acquisitionDate == default
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate invokes persistence on eligible asset (find/delete/save)")
  void generateInvokesPersistenceOnEligibleAsset() {
    // Arrange: table with useful life
    String username = "katherine_gutierrez";
    TaxTableSunat table = DataTest.getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = DataTest.getFinancialFixedAsset332ForPermanentAddition();

    // Stub lecturas previas para forzar deleteAll -> saveAll
    when(financialDepreciationService.findByFinancialFixedAssetIdService(eq(asset.getId())))
        .thenReturn(List.of());
    when(taxDepreciationService.findByFinancialFixedAssetIdService(eq(asset.getId())))
        .thenReturn(List.of());
    when(deductionService.findByTaxDepreciationInService(anyList()))
        .thenReturn(List.of());
    when(additionService.findByTaxDepreciationInService(anyList()))
        .thenReturn(List.of());

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: services called to persist projection lists
    verify(financialDepreciationService, atLeastOnce()).findByFinancialFixedAssetIdService(eq(asset.getId()));
    verify(financialDepreciationService, atLeastOnce()).deleteAllService(anyList());
    verify(financialDepreciationService, atLeastOnce()).saveAllService(anyList());

    verify(taxDepreciationService, atLeastOnce()).findByFinancialFixedAssetIdService(eq(asset.getId()));
    verify(taxDepreciationService, atLeastOnce()).deleteAllService(anyList());
    verify(taxDepreciationService, atLeastOnce()).saveAllService(anyList());

    verify(deductionService, atLeastOnce()).findByTaxDepreciationInService(anyList());
    verify(deductionService, atLeastOnce()).deleteAllService(anyList());
    verify(deductionService, atLeastOnce()).saveAllService(anyList());

    verify(additionService, atLeastOnce()).findByTaxDepreciationInService(anyList());
    verify(additionService, atLeastOnce()).deleteAllService(anyList());
    verify(additionService, atLeastOnce()).saveAllService(anyList());
  }

  @Test
  @DisplayName("generate does nothing when fixedAssets list is empty")
  void generateDoesNothingWhenNoAssets() {
    // Arrange
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    // Act
    generator.generate(username, LocalDate.now(), Collections.emptyList());

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
    String username = "katherine_gutierrez";
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
    generator.generate(username, LocalDate.now(), List.of(asset));

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
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "002");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ONE);
    asset.setWellAppraisedPreInvestmentStudyDry("sin término relevante");  // no contiene "tasacion" / "valor tasado" / "valortasado"

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

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
    String username = "katherine_gutierrez";
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
    generator.generate(username, LocalDate.now(), List.of(asset));

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
    String username = "katherine_gutierrez";
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
    generator.generate(username, LocalDate.now(), List.of(asset));

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
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "005");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setHistoricalCost(BigDecimal.ONE);
    asset.setRevaluedAmount(BigDecimal.ONE);
    asset.setInitialBalance(BigDecimal.ZERO);         // falla aquí
    asset.setWellAppraisedPreInvestmentStudyDry("valortasado");

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }
}