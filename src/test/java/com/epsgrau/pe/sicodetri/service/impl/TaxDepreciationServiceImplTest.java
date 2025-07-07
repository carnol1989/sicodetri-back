package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_02;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_03;
import static com.epsgrau.pe.sicodetri.util.DataTest.getTaxDepreciation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This class test for TaxDepreciationServiceImpl. <br/>
 * <b>Class</b>: {@link TaxDepreciationServiceImplTest}<br/>
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
 *     <li>Ago 28, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class TaxDepreciationServiceImplTest {

    @Mock
    private TaxDepreciationRepository repository;


    @InjectMocks
    private TaxDepreciationServiceImpl service;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveService returns the saved instance")
    void saveServiceReturnsSavedInstance() {
        // Arrange
        TaxDepreciation input = new TaxDepreciation();
        TaxDepreciation saved = getTaxDepreciation();
        when(repository.save(input)).thenReturn(saved);

        // Act
        TaxDepreciation result = service.saveService(input);

        // Assert
        verify(repository).save(input);
        assertSame(saved, result, "Debe devolver exactamente el objeto retornado por el repositorio");
    }

    @Test
    @DisplayName("saveAllService invokes repository.saveAll with given list")
    void saveAllServiceInvokesSaveAll() {
        // Arrange
        List<TaxDepreciation> list = List.of(getTaxDepreciation(), new TaxDepreciation());

        // Act
        service.saveAllService(list);

        // Assert
        verify(repository).saveAll(list);
    }

    @Test
    @DisplayName("Save taxDepreciation when valid taxDepreciation is provided")
    void saveTaxDepreciationWhenValidTaxDepreciationIsProvided() {
        // Arrange
        TaxDepreciation taxDepreciation = getTaxDepreciation();

        // Act
        service.saveService(taxDepreciation);

        // Assert
        verify(repository, times(1)).save(taxDepreciation);
    }

    @Test
    @DisplayName("Update taxDepreciation when valid taxDepreciation is provided")
    void updateTaxDepreciationWhenValidTaxDepreciationIsProvided() {
        // Arrange
        TaxDepreciation taxDepreciation = getTaxDepreciation();

        // Act
        service.updateService(taxDepreciation);

        // Assert
        verify(repository, times(1)).save(taxDepreciation);
    }

    @Test
    @DisplayName("Delete taxDepreciation when valid id is provided")
    void deleteTaxDepreciationWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Return taxDepreciation when it exists for given id")
    void returnTaxDepreciationWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        TaxDepreciation taxDepreciation = getTaxDepreciation();
        when(repository.findById(id)).thenReturn(Optional.of(taxDepreciation));

        // Act
        TaxDepreciation result = service.findByIdService(id);

        // Assert
        verify(repository).findById(id);
        assertEquals(taxDepreciation, result);
    }

    @Test
    @DisplayName("Return new taxDepreciation when none exists for given id")
    void returnNewTaxDepreciationWhenNoneExistsForGivenId() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Act
        TaxDepreciation result = service.findByIdService(1L);

        // Assert
        verify(repository).findById(1L);
        assertNotNull(result);
        assertNull(result.getId());
    }

    @Test
    @DisplayName("Return all taxDepreciations when findAllService is called")
    void returnAllTaxDepreciationsWhenFindAllServiceIsCalled() {
        // Arrange
        TaxDepreciation taxDepreciation = getTaxDepreciation();
        when(repository.findAll()).thenReturn(Collections.singletonList(taxDepreciation));

        // Act
        List<TaxDepreciation> result = service.findAllService();

        // Assert
        verify(repository).findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(taxDepreciation, result.get(0));
    }

    @Test @DisplayName("deleteAllService invokes repository.deleteAll")
    void deleteAllServiceInvokesDeleteAll() {
        List<TaxDepreciation> list = List.of(getTaxDepreciation(), new TaxDepreciation());
        service.deleteAllService(list);
        verify(repository).deleteAll(list);
    }

    @Test @DisplayName("findByFinancialFixedAssetIdService returns list")
    void findByFinancialFixedAssetIdServiceReturnsList() {
        long assetId = 123L;
        List<TaxDepreciation> expected = List.of(getTaxDepreciation());
        when(repository.findByFinancialFixedAssetId(assetId)).thenReturn(expected);

        List<TaxDepreciation> result = service.findByFinancialFixedAssetIdService(assetId);

        verify(repository).findByFinancialFixedAssetId(assetId);
        assertSame(expected, result);
    }

    @Test
    @DisplayName("findByFinancialFixedAssetIdService returns empty list when none")
    void findByFinancialFixedAssetIdServiceReturnsEmpty() {
        long assetId = 456L;
        when(repository.findByFinancialFixedAssetId(assetId)).thenReturn(Collections.emptyList());

        List<TaxDepreciation> result = service.findByFinancialFixedAssetIdService(assetId);

        verify(repository).findByFinancialFixedAssetId(assetId);
        assertTrue(result.isEmpty());
    }

    @Test @DisplayName("AllArgsConstructor and getters")
    void allArgsConstructorAndGetters() {
        Long id = 7L;
        LocalDate period = LocalDate.of(2025,6,30);
        BigDecimal aap = new BigDecimal("1000.00");
        BigDecimal antv = new BigDecimal("800.00");
        BigDecimal vd   = new BigDecimal("50.00");
        BigDecimal ntv  = new BigDecimal("750.00");
        FinancialFixedAsset ffa = new FinancialFixedAsset();
        List<Addition> adds       = List.of(new Addition());
        List<Deduction> deducts   = List.of(new Deduction());
        String addedBy    = "USERA";
        LocalDateTime adt = LocalDateTime.now().minusDays(1);
        String modBy      = "USERB";
        LocalDateTime mdt = adt.plusHours(5);
        boolean first     = true;
        boolean last      = false;

        TaxDepreciation td = new TaxDepreciation(
            id,
            period,
            aap,
            antv,
            vd,
            ntv,
            ffa,
            adds,
            deducts,
            addedBy,
            adt,
            modBy,
            mdt,
            first,
            last
        );

        assertEquals(id,   td.getId());
        assertEquals(period, td.getAccountingPeriod());
        assertEquals(aap,  td.getAnnualAccumulatedDepreciationPriorPeriod());
        assertEquals(antv, td.getAnnualNetTaxValuePriorPeriod());
        assertEquals(vd,   td.getValueDepreciation());
        assertEquals(ntv,  td.getNetTaxValue());
        assertSame(ffa,    td.getFinancialFixedAsset());
        assertSame(adds,   td.getAdditionsList());
        assertSame(deducts,td.getDeductionsList());
        assertEquals(addedBy, td.getAddedBy());
        assertEquals(adt,     td.getAdditionDate());
        assertEquals(modBy,   td.getModifiedBy());
        assertEquals(mdt,     td.getModificationDate());
        assertTrue(td.isFirst());
        assertFalse(td.isLast());
    }

    @Test @DisplayName("calculateDepreciation TEMPORARY_DEDUCTION")
    void calculateDepreciationType01() {
        // preparas activo y tabla
        FinancialFixedAsset fa = new FinancialFixedAsset();
        fa.setInitialBalance(new BigDecimal("1000.00"));
        fa.setHistoricalCost(new BigDecimal("2000.00"));
        fa.setAcquisitionDate(LocalDate.of(2024,6,27));
        fa.setUsefulLifeYears(10.0);

        TaxTableSunat table = new TaxTableSunat();
        table.setAnnualRate(12);         // 12%
        table.setUsefulLifeYears(10);

        TaxDepreciation old = new TaxDepreciation();
        old.setNetTaxValue(new BigDecimal("100.00"));
        old.setFirst(false);

        TaxDepreciation td = new TaxDepreciation();
        td.calculateDepreciation(fa, table, LocalDate.of(2025,6,27), old, S_VAL_01);

        // monthly rate = (12/100)/12 = 0.01
        // valueDepreciation = 1000 * 0.01 = 10.00
        assertEquals(new BigDecimal("10.00"), td.getValueDepreciation());
        // netTaxValue = old(100) - 10 = 90.00
        assertEquals(new BigDecimal("90.00"), td.getNetTaxValue());
        // annualAccumulated = 1000 * 0.12 * 1 = 120.00
        assertEquals(new BigDecimal("120.00"), td.getAnnualAccumulatedDepreciationPriorPeriod());
    }

    @Test @DisplayName("calculateDepreciation TEMPORARY_ADDITION")
    void calculateDepreciationType02() {
        FinancialFixedAsset fa = new FinancialFixedAsset();
        fa.setInitialBalance(new BigDecimal("500.00"));
        fa.setHistoricalCost(new BigDecimal("500.00"));
        fa.setAcquisitionDate(LocalDate.of(2024,6,27));
        fa.setUsefulLifeYears(5.0);

        TaxTableSunat table = new TaxTableSunat();
        table.setAnnualRate(12);
        table.setUsefulLifeYears(5);

        TaxDepreciation old = new TaxDepreciation();
        old.setNetTaxValue(BigDecimal.ZERO);
        old.setFirst(true);

        TaxDepreciation td = new TaxDepreciation();
        td.calculateDepreciation(fa, table, LocalDate.of(2025,6,27), old, S_VAL_02);

        // annualAccumulated = 0
        assertEquals(BigDecimal.ZERO.setScale(2), td.getAnnualAccumulatedDepreciationPriorPeriod());
        // annualNetTaxValuePrior = initialBalance = 500.00
        assertEquals(new BigDecimal("500.00"), td.getAnnualNetTaxValuePriorPeriod());
        // valueDepreciation = 500 * 0.01 = 5.00
        assertEquals(new BigDecimal("5.00"), td.getValueDepreciation());
        // netTaxValue (isFirst & type02) = initialBalance - valueDepreciation = 495.00
        assertEquals(new BigDecimal("495.00"), td.getNetTaxValue());
    }

    @Test @DisplayName("calculateDepreciation PERMANENT_ADDITIONS, old.isFirst=false & netTaxValue=0")
    void calculateDepreciationType03CaseZero() {
        FinancialFixedAsset fa = new FinancialFixedAsset();
        fa.setInitialBalance(new BigDecimal("300.00"));
        fa.setHistoricalCost(new BigDecimal("300.00"));
        fa.setAcquisitionDate(LocalDate.of(2024,6,27));
        fa.setUsefulLifeYears(3.0);

        TaxTableSunat table = new TaxTableSunat();
        table.setAnnualRate(12);
        table.setUsefulLifeYears(3);

        TaxDepreciation old = new TaxDepreciation();
        old.setNetTaxValue(BigDecimal.ZERO);
        old.setFirst(false);

        TaxDepreciation td = new TaxDepreciation();
        td.calculateDepreciation(fa, table, LocalDate.of(2025,6,27), old, S_VAL_03);

        // since old.netTax=0 && !isFirst, valueDepreciation = 0
        assertEquals(BigDecimal.ZERO.setScale(2), td.getValueDepreciation());
        assertEquals(BigDecimal.ZERO.setScale(2), td.getNetTaxValue());
    }

    @Test @DisplayName("calculateDepreciation PERMANENT_ADDITIONS, old.isFirst=true & netTaxValue>0")
    void calculateDepreciationType03CasePositive() {
        FinancialFixedAsset fa = new FinancialFixedAsset();
        fa.setInitialBalance(new BigDecimal("400.00"));
        fa.setHistoricalCost(new BigDecimal("400.00"));
        fa.setAcquisitionDate(LocalDate.of(2024,6,27));
        fa.setUsefulLifeYears(4.0);

        TaxTableSunat table = new TaxTableSunat();
        table.setAnnualRate(12);
        table.setUsefulLifeYears(4);

        TaxDepreciation old = new TaxDepreciation();
        old.setNetTaxValue(new BigDecimal("50.00"));
        old.setFirst(true);

        TaxDepreciation td = new TaxDepreciation();
        td.calculateDepreciation(fa, table, LocalDate.of(2025,6,27), old, S_VAL_03);

        // monthly = (12/100)/12 = 0.01; value = 400*0.01 = 4.00
        assertEquals(new BigDecimal("4.00"), td.getValueDepreciation());
        // annualAccumulated = historicalCost*0.12*1 = 48.00
        assertEquals(new BigDecimal("48.00"), td.getAnnualAccumulatedDepreciationPriorPeriod());
        // annualNetTaxValuePrior = historicalCost - 48 = 352.00
        assertEquals(new BigDecimal("352.00"), td.getAnnualNetTaxValuePriorPeriod());
        // netTaxValue = old(50) - value(4) = 46.00
        assertEquals(new BigDecimal("46.00"), td.getNetTaxValue());
    }

}