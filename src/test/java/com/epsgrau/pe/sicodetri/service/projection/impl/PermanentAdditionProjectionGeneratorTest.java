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

/**
 * Unit tests for {@link PermanentAdditionProjectionGenerator}.
 *
 * <p>Verifies that this generator:
 * <ul>
 *   <li>Reports its correct projection type.</li>
 *   <li>Skips generation when preconditions are not met (missing table, invalid asset data).</li>
 *   <li>Invokes persistence services for eligible assets.</li>
 *   <li>Handles empty input lists gracefully.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link PermanentAdditionProjectionGeneratorTest}<br/>
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
 *     <li>Jun 25, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
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

  /**
   * Initializes Mockito mocks before each test.
   */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Verifies that {@code getType()} returns the PERMANENT_ADDITIONS projection type.
   */
  @Test
  @DisplayName("getType returns PERMANENT_ADDITIONS")
  void getTypeReturnsPermanentAdditions() {
    assertEquals(ProjectionType.PERMANENT_ADDITIONS, generator.getType());
  }

  /**
   * Ensures that generation is skipped when no tax table is found for the asset's account prefix.
   */
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

  /**
   * Ensures that assets with default acquisition date or missing pre-investment description are skipped.
   */
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

  /**
   * Verifies that persistence services are invoked for a fully eligible asset.
   */
  @Test
  @DisplayName("generate invokes persistence on eligible asset")
  void generateInvokesPersistenceOnEligibleAsset() {
    // Arrange: table with useful life
    String username = "katherine_gutierrez";
    TaxTableSunat table = DataTest.getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(table);

    FinancialFixedAsset asset = DataTest.getFinancialFixedAsset332ForPermanentAddition();

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: services called to persist projection lists
    verify(financialDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(taxDepreciationService, atLeastOnce()).saveAllService(anyList());
    verify(deductionService, atLeastOnce()).saveAllService(anyList());
    verify(additionService, atLeastOnce()).saveAllService(anyList());
  }

  /**
   * Ensures that an empty asset list causes no interactions.
   */
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

  /**
   * Verifies skipping of assets missing a pre-investment description.
   */
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

  /**
   * Verifies skipping of assets whose description does not contain the required keyword.
   */
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
    asset.setWellAppraisedPreInvestmentStudyDry("sin término relevante");  // no contiene "tasacion"

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

  /**
   * Verifies skipping of assets with zero historical cost.
   */
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

  /**
   * Verifies skipping of assets with zero revalued amount.
   */
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

  /**
   * Verifies skipping of assets with zero initial balance.
   */
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
}