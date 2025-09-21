package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_9;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.BD_VAL_100;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.N_VAL_2;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_01;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_02;
import static com.epsgrau.pe.sicodetri.util.ConstantsTest.S_VAL_03;
import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialDepreciation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.entity.FinancialFixedAsset;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Unit tests for {@link FinancialDepreciationServiceImpl}.
 *
 * <p>This suite verifies the service-layer behavior for financial depreciation
 * operations, exercising CRUD flows, repository delegation, query semantics,
 * and the domain-specific depreciation calculation logic.</p>
 *
 * <p><b>Test coverage</b>:</p>
 * <ul>
 *   <li>Persistence: save single entity, save batch, update, delete by ID (with explicit flush),
 *       and delete batch (with explicit flush).</li>
 *   <li>Retrieval: find by ID (present and absent → fallback object), find all,
 *       and query by owning FinancialFixedAsset ID (results and empty).</li>
 *   <li>Computation: validates depreciation amounts and annual/monthly rates under
 *       multiple type codes (e.g., {@code S_VAL_01}, {@code S_VAL_02}, {@code S_VAL_03})
 *       and boundary conditions (years within/beyond useful life).</li>
 *   <li>Model integrity: all-args constructor, getters/setters, and basic state assertions.</li>
 * </ul>
 *
 * <p><b>Approach & tools</b>:</p>
 * <ul>
 *   <li>JUnit 5 (Jupiter) for the test framework</li>
 *   <li>Mockito for repository mocking and interaction verification</li>
 *   <li>AAA (Arrange–Act–Assert) structure; no external I/O</li>
 * </ul>
 *
 * <b>Class</b>: {@link FinancialDepreciationServiceImplTest}<br/>
 * <b>Company</b>: EPS Grau<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau
 *
 * @author EPS Grau
 * @since 1.0
 * @version 1.0
 */
class FinancialDepreciationServiceImplTest {

    @Mock
    private FinancialDepreciationRepository repository;

    @InjectMocks
    private FinancialDepreciationServiceImpl financialDepreciationService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("SaveService returns the saved instance")
    void saveServiceReturnsSavedInstance() {
        // Arrange
        FinancialDepreciation input = new FinancialDepreciation();
        FinancialDepreciation saved = getFinancialDepreciation();
        when(repository.save(input)).thenReturn(saved);

        // Act
        FinancialDepreciation result = financialDepreciationService.saveService(input);

        // Assert
        verify(repository).save(input);
        assertSame(saved, result, "Debe devolver exactamente el objeto retornado por el repositorio");
    }

    @Test
    @DisplayName("SaveAllService invokes repository.saveAll with given list")
    void saveAllServiceInvokesSaveAll() {
        // Arrange
        List<FinancialDepreciation> list = List.of(
            getFinancialDepreciation(),
            new FinancialDepreciation()
        );

        // Act
        financialDepreciationService.saveAllService(list);

        // Assert
        verify(repository).saveAll(list);
    }

    @Test
    @DisplayName("Save financial depreciation cuando se proporciona un objeto válido")
    void saveFinancialDepreciationWhenValidObjectIsProvided() {
        // Arrange
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();

        // Act
        financialDepreciationService.saveService(financialDepreciation);

        // Assert
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    @DisplayName("Update financial depreciation cuando se proporciona un objeto válido")
    void updateFinancialDepreciationWhenValidObjectIsProvided() {
        // Arrange
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();

        // Act
        financialDepreciationService.updateService(financialDepreciation);

        // Assert
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    @DisplayName("Delete financial depreciation cuando el id es válido (incluye flush)")
    void deleteFinancialDepreciationWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        financialDepreciationService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
        verify(repository, times(1)).flush(); // cubrir la línea de flush
    }

    @Test
    @DisplayName("Return financial depreciation cuando existe el id")
    void returnFinancialDepreciationWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        FinancialDepreciation fd = getFinancialDepreciation();
        when(repository.findById(id)).thenReturn(Optional.of(fd));

        // Act
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(fd, result);
        assertEquals(fd.getId(), result.getId());
        assertEquals(fd.getAccountingPeriod(), result.getAccountingPeriod());
        assertEquals(fd.getValueDepreciation(), result.getValueDepreciation());
        assertEquals(fd.getAnnualFinancialRate(), result.getAnnualFinancialRate());
        assertEquals(fd.getAddedBy(), result.getAddedBy());
        assertEquals(fd.getAdditionDate(), result.getAdditionDate());
        assertEquals(fd.getModifiedBy(), result.getModifiedBy());
        assertEquals(fd.getModificationDate(), result.getModificationDate());
    }

    @Test
    @DisplayName("Return new financial depreciation cuando no existe el id")
    void returnNewFinancialDepreciationWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
        result.setId(77L);
        assertEquals(77L, result.getId());
        LocalDate ap = LocalDate.of(2025, 6, 26);
        result.setAccountingPeriod(ap);
        assertEquals(ap, result.getAccountingPeriod());
        BigDecimal vd = new BigDecimal("321.98");
        result.setValueDepreciation(vd);
        assertEquals(vd, result.getValueDepreciation());
        BigDecimal afr = new BigDecimal("0.1234");
        result.setAnnualFinancialRate(afr);
        assertEquals(afr, result.getAnnualFinancialRate());
        result.setAddedBy("USR");
        assertEquals("USR", result.getAddedBy());
        LocalDateTime dt1 = LocalDateTime.now().minusHours(3);
        result.setAdditionDate(dt1);
        assertEquals(dt1, result.getAdditionDate());
        result.setModifiedBy("MOD");
        assertEquals("MOD", result.getModifiedBy());
        LocalDateTime dt2 = dt1.plusDays(1);
        result.setModificationDate(dt2);
        assertEquals(dt2, result.getModificationDate());
    }

    @Test
    @DisplayName("Return all financial depreciations cuando se llama findAllService")
    void returnAllFinancialDepreciationsWhenFindAllServiceIsCalled() {
        // Arrange
        List<FinancialDepreciation> financialDepreciations = List.of(new FinancialDepreciation(), new FinancialDepreciation());
        when(repository.findAll()).thenReturn(financialDepreciations);

        // Act
        List<FinancialDepreciation> result = financialDepreciationService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
        assertEquals(financialDepreciations, result);
    }

    @Test
    @DisplayName("Delete all financial depreciations cuando se proporciona una lista (incluye flush)")
    void deleteAllFinancialDepreciationsWhenListIsProvided() {
        // Arrange
        List<FinancialDepreciation> list = List.of(
            getFinancialDepreciation(),
            new FinancialDepreciation()
        );

        // Act
        financialDepreciationService.deleteAllService(list);

        // Assert
        verify(repository, times(1)).deleteAll(list);
        verify(repository, times(1)).flush(); // cubrir la línea de flush
    }

    @Test
    @DisplayName("Find by financialFixedAssetId retorna el resultado del repositorio")
    void findByFinancialFixedAssetIdServiceReturnsList() {
        // Arrange
        Long fixedAssetId = 123L;
        List<FinancialDepreciation> expected = List.of(getFinancialDepreciation());
        when(repository.findByFinancialFixedAssetId(fixedAssetId)).thenReturn(expected);

        // Act
        List<FinancialDepreciation> result =
            financialDepreciationService.findByFinancialFixedAssetIdService(fixedAssetId);

        // Assert
        verify(repository, times(1)).findByFinancialFixedAssetId(fixedAssetId);
        assertSame(expected, result, "Debe devolver exactamente la lista que retorna el repositorio");
    }

    @Test
    @DisplayName("Find by financialFixedAssetId retorna lista vacía cuando no hay resultados")
    void findByFinancialFixedAssetIdServiceReturnsEmptyList() {
        // Arrange
        Long fixedAssetId = 456L;
        when(repository.findByFinancialFixedAssetId(fixedAssetId))
            .thenReturn(Collections.emptyList());

        // Act
        List<FinancialDepreciation> result =
            financialDepreciationService.findByFinancialFixedAssetIdService(fixedAssetId);

        // Assert
        verify(repository, times(1)).findByFinancialFixedAssetId(fixedAssetId);
        assertTrue(result.isEmpty(), "Cuando no hay depreciaciones, debe devolver lista vacía");
    }

    @Test
    @DisplayName("calculateDepreciation con S_VAL_01 o S_VAL_03 calcula el valor correcto")
    void calculateDepreciationType01Or03() {
        FinancialFixedAsset asset = new FinancialFixedAsset();
        asset.setInitialBalance(new BigDecimal("1200.00"));
        asset.setUsefulLifeYears(12.0);

        FinancialDepreciation fd = new FinancialDepreciation();
        fd.calculateDepreciation(asset, S_VAL_01, 5);

        // annual rate = (100 / 12) / 100 = 0.0833333333
        BigDecimal expectedAnnual = BD_VAL_100
            .divide(BigDecimal.valueOf(12.0), N_VAL_9, RoundingMode.HALF_UP)
            .divide(BD_VAL_100, N_VAL_9, RoundingMode.HALF_UP);
        assertEquals(expectedAnnual.setScale(N_VAL_9, RoundingMode.HALF_UP), fd.getAnnualFinancialRate());

        // monthly rate = annual / 12 = ~0.006944444
        // valueDepreciation = 1200 * monthly rate = 8.3333333 -> 8.33
        assertEquals(new BigDecimal("8.33"), fd.getValueDepreciation());

        // idem para S_VAL_03
        fd = new FinancialDepreciation();
        fd.calculateDepreciation(asset, S_VAL_03, 1);
        assertEquals(new BigDecimal("8.33"), fd.getValueDepreciation());
    }

    @Test
    @DisplayName("calculateDepreciation con S_VAL_02 y years <= usefulLife calcula el mismo valor")
    void calculateDepreciationType02WithinYears() {
        FinancialFixedAsset asset = new FinancialFixedAsset();
        asset.setInitialBalance(new BigDecimal("1000.00"));
        asset.setUsefulLifeYears(10.0);

        FinancialDepreciation fd = new FinancialDepreciation();
        fd.calculateDepreciation(asset, S_VAL_02, 5);

        assertEquals(new BigDecimal("8.33"), fd.getValueDepreciation());
    }

    @Test
    @DisplayName("calculateDepreciation con S_VAL_02 y years > usefulLife establece cero")
    void calculateDepreciationType02BeyondYears() {
        FinancialFixedAsset asset = new FinancialFixedAsset();
        asset.setInitialBalance(new BigDecimal("500.00"));
        asset.setUsefulLifeYears(5.0);

        FinancialDepreciation fd = new FinancialDepreciation();
        fd.calculateDepreciation(asset, S_VAL_02, 6);

        assertEquals(BigDecimal.ZERO.setScale(N_VAL_2, RoundingMode.DOWN), fd.getValueDepreciation());
    }

    @Test
    @DisplayName("FinancialDepreciation AllArgsConstructor y getters")
    void financialDepreciationAllArgsConstructorAndGetters() {
        // Arrange
        Long id = 99L;
        LocalDate period = LocalDate.of(2025, 1, 1);
        BigDecimal val = new BigDecimal("123.45");
        BigDecimal rate = new BigDecimal("0.1234");
        FinancialFixedAsset asset = new FinancialFixedAsset();
        asset.setInitialBalance(new BigDecimal("1000.00"));
        asset.setUsefulLifeYears(10.0);
        String addedBy = "USER_A";
        LocalDateTime addDate = LocalDateTime.of(2025, 1, 2, 10, 30);
        String modifiedBy = "USER_B";
        LocalDateTime modDate = addDate.plusHours(5);

        // Act
        FinancialDepreciation fd = new FinancialDepreciation(
            id,
            period,
            val,
            rate,
            asset,
            addedBy,
            addDate,
            modifiedBy,
            modDate
        );

        // Assert getters
        assertEquals(id,        fd.getId());
        assertEquals(period,    fd.getAccountingPeriod());
        assertEquals(val,       fd.getValueDepreciation());
        assertEquals(rate,      fd.getAnnualFinancialRate());
        assertSame(asset,       fd.getFinancialFixedAsset());
        assertEquals(addedBy,   fd.getAddedBy());
        assertEquals(addDate,   fd.getAdditionDate());
        assertEquals(modifiedBy,fd.getModifiedBy());
        assertEquals(modDate,   fd.getModificationDate());
    }

    @Test
    @DisplayName("FinancialDepreciation setters y getters")
    void financialDepreciationSettersAndGetters() {
        // Arrange
        FinancialDepreciation fd = new FinancialDepreciation();

        Long id = 77L;
        LocalDate period = LocalDate.of(2024, 12, 31);
        BigDecimal val = new BigDecimal("543.21");
        BigDecimal rate = new BigDecimal("0.0543");
        FinancialFixedAsset asset = new FinancialFixedAsset();
        String addedBy = "USER_X";
        LocalDateTime addDate = LocalDateTime.now().minusDays(1);
        String modifiedBy = "USER_Y";
        LocalDateTime modDate = addDate.plusMinutes(90);

        // Act – setters
        fd.setId(id);
        fd.setAccountingPeriod(period);
        fd.setValueDepreciation(val);
        fd.setAnnualFinancialRate(rate);
        fd.setFinancialFixedAsset(asset);
        fd.setAddedBy(addedBy);
        fd.setAdditionDate(addDate);
        fd.setModifiedBy(modifiedBy);
        fd.setModificationDate(modDate);

        // Assert getters
        assertEquals(id,         fd.getId());
        assertEquals(period,     fd.getAccountingPeriod());
        assertEquals(val,        fd.getValueDepreciation());
        assertEquals(rate,       fd.getAnnualFinancialRate());
        assertSame(asset,        fd.getFinancialFixedAsset());
        assertEquals(addedBy,    fd.getAddedBy());
        assertEquals(addDate,    fd.getAdditionDate());
        assertEquals(modifiedBy, fd.getModifiedBy());
        assertEquals(modDate,    fd.getModificationDate());
    }

}