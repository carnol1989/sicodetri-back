package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getDeduction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
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
 * Unit tests for {@link DeductionServiceImpl}.
 *
 * <p>
 * Verifies CRUD operations and query methods provided by the DeductionServiceImpl:
 * <ul>
 *   <li>Saving a single deduction and returning the persisted instance</li>
 *   <li>Saving a list of deductions</li>
 *   <li>Updating and deleting by ID</li>
 *   <li>Retrieving by ID—existing and non-existing cases</li>
 *   <li>Retrieving all deductions</li>
 *   <li>Deleting all deductions in a list</li>
 *   <li>Finding deductions by a list of TaxDepreciation entities</li>
 *   <li>All-args constructor correctness</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link DeductionServiceImplTest}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class DeductionServiceImplTest {

    @Mock
    private DeductionRepository repository;

    @InjectMocks
    private DeductionServiceImpl deductionService;

    /**
     * Initializes Mockito annotations before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#saveService(Deduction)}
     * returns the exact instance provided by the repository.
     */
    @Test
    @DisplayName("SaveService returns the saved instance")
    void saveServiceReturnsSavedInstance() {
        // Arrange
        Deduction input = new Deduction();
        Deduction saved = getDeduction();
        when(repository.save(input)).thenReturn(saved);

        // Act
        Deduction result = deductionService.saveService(input);

        // Assert
        verify(repository).save(input);
        assertSame(saved, result, "Debe devolver el objeto retornado por el repositorio");
    }

    /**
     * Verifies that {@link DeductionServiceImpl#saveAllService(List)}
     * calls {@code repository.saveAll} exactly once with the provided list.
     */
    @Test
    @DisplayName("Save all deductions when list is provided")
    void saveAllDeductionsWhenListIsProvided() {
        // Arrange
        List<Deduction> list = List.of(new Deduction(), new Deduction());

        // Act
        deductionService.saveAllService(list);

        // Assert
        verify(repository).saveAll(list);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#saveService(Deduction)}
     * delegates to {@code repository.save} when saving a valid deduction.
     */
    @Test
    @DisplayName("Save deduction when valid deduction is provided")
    void saveDeductionWhenValidDeductionIsProvided() {
        // Arrange
        Deduction deduction = new Deduction();

        // Act
        deductionService.saveService(deduction);

        // Assert
        verify(repository, times(1)).save(deduction);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#updateService(Deduction)}
     * delegates to {@code repository.save} when updating a valid deduction.
     */
    @Test
    @DisplayName("Update deduction when valid deduction is provided")
    void updateDeductionWhenValidDeductionIsProvided() {
        // Arrange
        Deduction deduction = new Deduction();

        // Act
        deductionService.updateService(deduction);

        // Assert
        verify(repository, times(1)).save(deduction);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#deleteByIdService(Long)}
     * calls {@code repository.deleteById} for the given ID.
     */
    @Test
    @DisplayName("Delete deduction when valid id is provided")
    void deleteDeductionWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        deductionService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#findByIdService(Long)}
     * returns the entity found by {@code repository.findById}.
     */
    @Test
    @DisplayName("Return deduction when it exists for given id")
    void returnDeductionWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        Deduction deduction = getDeduction();
        when(repository.findById(id)).thenReturn(Optional.of(deduction));

        // Act
        Deduction result = deductionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(deduction, result);
        assertEquals(deduction.getId(),                  result.getId());
        assertEquals(deduction.getAccountingPeriod(),    result.getAccountingPeriod());
        assertEquals(deduction.getValueDeduction(),      result.getValueDeduction());
        assertSame  (deduction.getTaxDepreciation(),    result.getTaxDepreciation());
        assertEquals(deduction.getAddedBy(),            result.getAddedBy());
        assertEquals(deduction.getAdditionDate(),       result.getAdditionDate());
        assertEquals(deduction.getModifiedBy(),         result.getModifiedBy());
        assertEquals(deduction.getModificationDate(),   result.getModificationDate());
    }

    /**
     * Verifies that {@link DeductionServiceImpl#findByIdService(Long)}
     * returns a new, mutable {@link Deduction} when none exists in the repository.
     */
    @Test
    @DisplayName("Return new deduction when none exists for given id")
    void returnNewDeductionWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Deduction result = deductionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
        result.setId(42L);
        assertEquals(42L, result.getId());
        LocalDate ap = LocalDate.of(2025, 6, 26);
        result.setAccountingPeriod(ap);
        assertEquals(ap, result.getAccountingPeriod());
        BigDecimal vd = new BigDecimal("12345.67");
        result.setValueDeduction(vd);
        assertEquals(vd, result.getValueDeduction());
        TaxDepreciation td = new TaxDepreciation();
        result.setTaxDepreciation(td);
        assertSame(td, result.getTaxDepreciation());
        result.setAddedBy("USER1");
        assertEquals("USER1", result.getAddedBy());
        LocalDateTime dt1 = LocalDateTime.now().minusDays(1);
        result.setAdditionDate(dt1);
        assertEquals(dt1, result.getAdditionDate());
        result.setModifiedBy("USER2");
        assertEquals("USER2", result.getModifiedBy());
        LocalDateTime dt2 = dt1.plusHours(5);
        result.setModificationDate(dt2);
        assertEquals(dt2, result.getModificationDate());
    }

    /**
     * Verifies that {@link DeductionServiceImpl#findAllService()}
     * calls {@code repository.findAll} and returns a non-null list.
     */
    @Test
    @DisplayName("Return all deductions when findAllService is called")
    void returnAllDeductionsWhenFindAllServiceIsCalled() {
        // Arrange
        List<Deduction> deductions = List.of(new Deduction(), new Deduction());
        when(repository.findAll()).thenReturn(deductions);

        // Act
        List<Deduction> result = deductionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
        assertNotNull(result);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#deleteAllService(List)}
     * calls {@code repository.deleteAll} for the provided list.
     */
    @Test
    @DisplayName("Delete all deductions when list is provided")
    void deleteAllDeductionsWhenListIsProvided() {
        // Arrange
        List<Deduction> list = List.of(getDeduction(), new Deduction());

        // Act
        deductionService.deleteAllService(list);

        // Assert
        verify(repository, times(1)).deleteAll(list);
    }

    /**
     * Verifies that {@link DeductionServiceImpl#findByTaxDepreciationInService(List)}
     * returns the exact list provided by {@code repository.findByTaxDepreciationIn}.
     */
    @Test
    @DisplayName("Find deductions by TaxDepreciation list returns repository result")
    void findByTaxDepreciationInServiceReturnsList() {
        // Arrange
        List<TaxDepreciation> tdList = List.of(new TaxDepreciation(), new TaxDepreciation());
        List<Deduction> expected = List.of(getDeduction());
        when(repository.findByTaxDepreciationIn(tdList)).thenReturn(expected);

        // Act
        List<Deduction> result = deductionService.findByTaxDepreciationInService(tdList);

        // Assert
        verify(repository, times(1)).findByTaxDepreciationIn(tdList);
        assertSame(expected, result, "Debe devolver exactamente la lista que retorna el repositorio");
        assertEquals(expected.get(0).getValueDeduction(), result.get(0).getValueDeduction());
    }

    /**
     * Verifies that {@link DeductionServiceImpl#findByTaxDepreciationInService(List)}
     * returns an empty list when the repository yields no results.
     */
    @Test
    @DisplayName("Find deductions by TaxDepreciation returns empty list when none found")
    void findByTaxDepreciationInServiceReturnsEmptyList() {
        // Arrange
        List<TaxDepreciation> tdList = List.of(new TaxDepreciation());
        when(repository.findByTaxDepreciationIn(tdList)).thenReturn(Collections.emptyList());

        // Act
        List<Deduction> result = deductionService.findByTaxDepreciationInService(tdList);

        // Assert
        verify(repository, times(1)).findByTaxDepreciationIn(tdList);
        assertTrue(result.isEmpty(), "Cuando el repositorio devuelve vacío, el servicio también debe devolver lista vacía");
    }

    /**
     * Verifies that the Lombok-generated all-args constructor on {@link Deduction}
     * assigns all fields correctly.
     */
    @Test
    @DisplayName("Deduction AllArgsConstructor sets all fields correctly")
    void deductionAllArgsConstructorSetsAllFields() {
        // Arrange valores de prueba
        Long id = 99L;
        LocalDate period = LocalDate.of(2025, 6, 26);
        BigDecimal value = new BigDecimal("555.55");
        TaxDepreciation taxDep = new TaxDepreciation();
        String addedBy = "TEST_USER";
        LocalDateTime additionDate = LocalDateTime.of(2025, 6, 26, 14, 30);
        String modifiedBy = "MOD_USER";
        LocalDateTime modificationDate = additionDate.plusHours(2);

        // Act
        Deduction d = new Deduction(
            id,
            period,
            value,
            taxDep,
            addedBy,
            additionDate,
            modifiedBy,
            modificationDate
        );

        // Assert
        assertEquals(id,                  d.getId());
        assertEquals(period,              d.getAccountingPeriod());
        assertEquals(value,               d.getValueDeduction());
        assertSame  (taxDep,              d.getTaxDepreciation());
        assertEquals(addedBy,             d.getAddedBy());
        assertEquals(additionDate,        d.getAdditionDate());
        assertEquals(modifiedBy,          d.getModifiedBy());
        assertEquals(modificationDate,    d.getModificationDate());
    }

}