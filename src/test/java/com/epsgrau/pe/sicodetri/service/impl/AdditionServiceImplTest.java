package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getAddition;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link AdditionServiceImpl}.
 *
 * <p>
 * Verifies saving, updating, deleting, and retrieval operations on additions,
 * as well as handling of empty results and use of the all-args constructor.
 * </p>
 *
 * <b>Class</b>: {@link AdditionServiceImplTest}<br/>
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
class AdditionServiceImplTest {

    @Mock
    private AdditionRepository repository;

    @InjectMocks
    private AdditionServiceImpl additionService;

    /**
     * Constructs the test class and initializes Mockito mocks.
     */
    public AdditionServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#saveService(Addition)} calls
     * {@code repository.save} once when a valid addition is provided.
     */
    @Test
    @DisplayName("Save addition when valid addition is provided")
    void saveAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.saveService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#saveService(Addition)} returns
     * the exact instance returned by the repository and that all fields are non-null.
     */
    @Test
    @DisplayName("SaveService returns the saved instance")
    void saveServiceReturnsSavedInstance() {
        // Arrange
        Addition input = new Addition();
        Addition saved = getAddition();
        when(repository.save(input)).thenReturn(saved);

        // Act
        Addition result = additionService.saveService(input);

        // Assert
        verify(repository).save(input);
        assertSame(saved, result, "Debe devolver exactamente el objeto que retorna el repositorio");
        assertNotNull(result.getId());
        assertNotNull(result.getAccountingPeriod());
        assertNotNull(result.getValueAddition());
        assertNotNull(result.getTaxDepreciation());
        assertNotNull(result.getAddedBy());
        assertNotNull(result.getAdditionDate());
        assertNotNull(result.getModifiedBy());
        assertNotNull(result.getModificationDate());
    }

    /**
     * Verifies that {@link AdditionServiceImpl#saveAllService(List)} calls
     * {@code repository.saveAll} once when a list of additions is provided.
     */
    @Test
    @DisplayName("Save all additions when list is provided")
    void saveAllAdditionsWhenListIsProvided() {
        // Arrange
        List<Addition> list = List.of(new Addition(), new Addition());

        // Act
        additionService.saveAllService(list);

        // Assert
        verify(repository, times(1)).saveAll(list);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#updateService(Addition)} calls
     * {@code repository.save} once when updating an existing addition.
     */
    @Test
    @DisplayName("Update addition when valid addition is provided")
    void updateAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.updateService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#deleteByIdService(Long)} calls
     * {@code repository.deleteById} once for the given ID.
     */
    @Test
    @DisplayName("Delete addition when valid id is provided")
    void deleteAdditionWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        additionService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#findByIdService(Long)} returns
     * the existing addition when found in the repository.
     */
    @Test
    @DisplayName("Return addition when it exists for given id")
    void returnAdditionWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        Addition addition = getAddition();
        when(repository.findById(id)).thenReturn(Optional.of(addition));

        // Act
        Addition result = additionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(addition, result);
        assertEquals(addition.getId(), result.getId());
        assertEquals(addition.getAccountingPeriod(), result.getAccountingPeriod());
        assertEquals(addition.getValueAddition(), result.getValueAddition());
        assertEquals(addition.getTaxDepreciation(), result.getTaxDepreciation());
        assertEquals(addition.getAddedBy(), result.getAddedBy());
        assertEquals(addition.getAdditionDate(), result.getAdditionDate());
        assertEquals(addition.getModifiedBy(), result.getModifiedBy());
        assertEquals(addition.getModificationDate(), result.getModificationDate());
    }

    /**
     * Verifies that {@link AdditionServiceImpl#findByIdService(Long)} returns
     * a new, mutable {@link Addition} when the repository returns empty.
     */
    @Test
    @DisplayName("Return new addition when none exists for given id")
    void returnNewAdditionWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Addition result = additionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
        result.setId(42L);
        assertEquals(42L, result.getId());
        LocalDate ap = LocalDate.of(2025, 6, 26);
        result.setAccountingPeriod(ap);
        assertEquals(ap, result.getAccountingPeriod());
        BigDecimal va = new BigDecimal("1234.56");
        result.setValueAddition(va);
        assertEquals(va, result.getValueAddition());
        TaxDepreciation td = new TaxDepreciation();
        result.setTaxDepreciation(td);
        assertSame(td, result.getTaxDepreciation());
        result.setAddedBy("TESTER");
        assertEquals("TESTER", result.getAddedBy());
        LocalDateTime dt1 = LocalDateTime.now().minusDays(1);
        result.setAdditionDate(dt1);
        assertEquals(dt1, result.getAdditionDate());
        result.setModifiedBy("MODDER");
        assertEquals("MODDER", result.getModifiedBy());
        LocalDateTime dt2 = dt1.plusHours(5);
        result.setModificationDate(dt2);
        assertEquals(dt2, result.getModificationDate());
    }

    /**
     * Verifies that {@link AdditionServiceImpl#findAllService()} calls
     * {@code repository.findAll} once and returns all additions.
     */
    @Test
    @DisplayName("Return all additions when findAllService is called")
    void returnAllAdditionsWhenFindAllServiceIsCalled() {
        // Act
        additionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
    }

    /**
     * Verifies that {@link AdditionServiceImpl#deleteAllService(List)} calls
     * {@code repository.deleteAll} once when a list of additions is provided.
     */
    @Test
    @DisplayName("Delete all additions when list is provided")
    void deleteAllAdditionsWhenListIsProvided() {
        // Arrange
        List<Addition> list = List.of(getAddition(), new Addition());

        // Act
        additionService.deleteAllService(list);

        // Assert
        verify(repository, times(1)).deleteAll(list);
    }

    /**
     * Verifies that {@link AdditionServiceImpl#findByTaxDepreciationInService(List)}
     * returns the same list as the repository for a given list of tax depreciations.
     */
    @Test
    @DisplayName("Find additions by TaxDepreciation list returns repository result")
    void findByTaxDepreciationInServiceReturnsList() {
        // Arrange
        List<TaxDepreciation> tdList = List.of(new TaxDepreciation(), new TaxDepreciation());
        List<Addition> expected = List.of(getAddition());
        when(repository.findByTaxDepreciationIn(tdList)).thenReturn(expected);

        // Act
        List<Addition> result = additionService.findByTaxDepreciationInService(tdList);

        // Assert
        verify(repository, times(1)).findByTaxDepreciationIn(tdList);
        assertSame(expected, result, "Debe devolver exactamente la lista que retorna el repositorio");
        assertNotNull(result.get(0).getValueAddition());
    }

    /**
     * Verifies that the Lombok-generated all-args constructor on {@link Addition}
     * correctly assigns all fields.
     */
    @Test
    @DisplayName("Addition AllArgsConstructor sets all fields correctly")
    void additionAllArgsConstructorSetsAllFields() {
        // Arrange: valores de prueba
        Long id = 99L;
        LocalDate period = LocalDate.of(2025, 6, 26);
        BigDecimal value = new BigDecimal("555.55");
        TaxDepreciation taxDep = new TaxDepreciation();
        String addedBy = "TEST_USER";
        LocalDateTime additionDate = LocalDateTime.of(2025, 6, 26, 14, 30);
        String modifiedBy = "MOD_USER";
        LocalDateTime modificationDate = additionDate.plusHours(2);

        // Act: usamos el AllArgsConstructor generado por Lombok
        Addition a = new Addition(
            id,
            period,
            value,
            taxDep,
            addedBy,
            additionDate,
            modifiedBy,
            modificationDate
        );

        // Assert: cada getter devuelve el valor correspondiente
        assertEquals(id,                  a.getId());
        assertEquals(period,              a.getAccountingPeriod());
        assertEquals(value,               a.getValueAddition());
        assertSame  (taxDep,              a.getTaxDepreciation());
        assertEquals(addedBy,             a.getAddedBy());
        assertEquals(additionDate,        a.getAdditionDate());
        assertEquals(modifiedBy,          a.getModifiedBy());
        assertEquals(modificationDate,    a.getModificationDate());
    }

}