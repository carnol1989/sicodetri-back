package com.epsgrau.pe.sicodetri.service.projection.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.S_VAL_332;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialFixedAsset332ForTemporaryAddition;
import static com.epsgrau.pe.sicodetri.util.DataTest.getTaxTable332;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link TemporaryAdditionProjectionGenerator}.
 *
 * <p>These tests validate orchestration and service collaboration for the
 * <em>Temporary Additions</em> projection flow. Specifically, they verify that the
 * generator:</p>
 * <ul>
 *   <li>Declares the correct projection type ({@link ProjectionType#TEMPORARY_ADDITIONS}).</li>
 *   <li>Skips processing when the SUNAT tax table cannot be resolved for the account prefix.</li>
 *   <li>Filters out non-eligible assets (default acquisition date, zero initial balance,
 *       banned descriptors, or non-supported prefixes outside 332–337).</li>
 *   <li>Invokes the expected read–delete–save sequence on financial/tax depreciation,
 *       additions, and deductions for eligible assets.</li>
 * </ul>
 *
 * <p>The focus is on interaction contracts and control-flow decisions; numerical
 * depreciation calculations are covered elsewhere.</p>
 *
 * <b>Class</b>: {@link TemporaryAdditionProjectionGeneratorTest}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <br/><u>Service Provider</u>: Consultor TI
 *     <br/><u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
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
    String username = "katherine_gutierrez";
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "XYZ");
    // taxTableService returns null
    when(taxTableService.findByAccountigAccountService(S_VAL_332)).thenReturn(null);

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: no services should be invoked
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate skips non-eligible assets")
  void generateSkipsNonEligible() {
    // Arrange: tax table present
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());
    // Asset not eligible: acquisitionDate == LOCAL_DATE_DEFAULT y saldo inicial 0
    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setAcquisitionDate(LOCAL_DATE_DEFAULT);
    asset.setInitialBalance(BigDecimal.ZERO);

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: still no interactions
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate invokes services on eligible asset (incluye find/delete/save)")
  void generateInvokesServicesWhenEligible() {
    // Arrange: tax table con usefulLifeYears = 2
    String username = "katherine_gutierrez";
    TaxTableSunat table = getTaxTable332();
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(table);

    FinancialFixedAsset asset = getFinancialFixedAsset332ForTemporaryAddition();
    // Fuerza rama desc == null en isEligible
    asset.setWellAppraisedPreInvestmentStudyDry(null);

    // Stubs de lecturas previas (para que deleteAll reciba una lista)
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

    // Assert: cada etapa (read->delete->save) es invocada
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
  @DisplayName("generate skips assets with banned description")
  void generateSkipsWhenDescriptionContainsBanned() {
    // Arrange: tax table present
    String username = "katherine_gutierrez";
    when(taxTableService.findByAccountigAccountService(S_VAL_332))
        .thenReturn(new TaxTableSunat());

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount(S_VAL_332 + "123");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setInitialBalance(new BigDecimal("100"));
    asset.setWellAppraisedPreInvestmentStudyDry("Valor Tasado premium"); // banned

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: skip all
    verifyNoInteractions(financialDepreciationService, taxDepreciationService, additionService, deductionService);
  }

  @Test
  @DisplayName("generate skips assets with non-eligible accounting-account prefix")
  void generateSkipsWhenPrefixNotEligible() {
    // Arrange: la tabla existe, pero el prefijo "999" no está entre 332–337
    String username = "katherine_gutierrez";
    TaxTableSunat table = getTaxTable332();
    when(taxTableService.findByAccountigAccountService("999")).thenReturn(table);

    FinancialFixedAsset asset = new FinancialFixedAsset();
    asset.setFixedAssetAccountingAccount("9991234567");
    asset.setAcquisitionDate(LocalDate.now().minusYears(1));
    asset.setInitialBalance(new BigDecimal("100"));
    asset.setWellAppraisedPreInvestmentStudyDry(""); // descripción válida

    // Act
    generator.generate(username, LocalDate.now(), List.of(asset));

    // Assert: al no ser elegible por prefijo, no se invocan servicios
    verifyNoInteractions(
        financialDepreciationService,
        taxDepreciationService,
        additionService,
        deductionService
    );
  }
}