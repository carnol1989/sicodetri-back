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
 * Unit test suite for {@link AdditionServiceImpl}.
 *
 * <p><b>Purpose</b>:
 * Validates the service's behavior for CRUD operations and repository delegation,
 * including flush semantics where applicable. These tests focus on interaction
 * verification and data mapping rather than database integration.</p>
 *
 * <p><b>Coverage</b>:</p>
 * <ul>
 *   <li><b>Create</b>: {@code saveService} returns the repository result and forwards the entity.</li>
 *   <li><b>Bulk create</b>: {@code saveAllService} delegates to {@code saveAll}.</li>
 *   <li><b>Update</b>: {@code updateService} delegates to {@code save} (merge semantics).</li>
 *   <li><b>Delete</b>: {@code deleteByIdService} and {@code deleteAllService} delegate to repository and invoke {@code flush()}.</li>
 *   <li><b>Read</b>: {@code findByIdService} returns the entity when present or a new empty instance when absent.</li>
 *   <li><b>Read all</b>: {@code findAllService} returns exactly the list from the repository.</li>
 *   <li><b>Query by relation</b>: {@code findByTaxDepreciationInService} delegates to the repository predicate.</li>
 *   <li><b>DTO/Entity mapping sanity</b>: Exercises the {@code Addition} all-args constructor and getters.</li>
 * </ul>
 *
 * <p><b>Test doubles & tools</b>:</p>
 * <ul>
 *   <li>Mockito for mocks and stubbing ({@link AdditionRepository}).</li>
 *   <li>JUnit 5 (Jupiter) for test lifecycle and assertions.</li>
 * </ul>
 *
 * <p><b>Notes</b>:
 * Display names are provided (some in Spanish) to make intent explicit.
 * The suite intentionally asserts that service methods do not return {@code null}
 * unless explicitly documented to do so.</p>
 *
 * <b>Class</b>: {@link AdditionServiceImplTest}<br/>
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
 *       <li>Jun 06, 2025 (JAR) Class creation.</li>
 *     </ul>
 * @version 1.0
 * @since 1.0
 */
class AdditionServiceImplTest {

    @Mock
    private AdditionRepository repository;

    @InjectMocks
    private AdditionServiceImpl additionService;

    public AdditionServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save addition cuando se provee una addition válida")
    void saveAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.saveService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    @Test
    @DisplayName("SaveService retorna la instancia guardada")
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

    @Test
    @DisplayName("Save all additions cuando se provee una lista")
    void saveAllAdditionsWhenListIsProvided() {
        // Arrange
        List<Addition> list = List.of(new Addition(), new Addition());

        // Act
        additionService.saveAllService(list);

        // Assert
        verify(repository, times(1)).saveAll(list);
    }

    @Test
    @DisplayName("Update addition cuando se provee una addition válida")
    void updateAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.updateService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    @Test
    @DisplayName("Delete addition cuando se provee un id válido (incluye flush)")
    void deleteAdditionWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        additionService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
        verify(repository, times(1)).flush(); // cubrir flush()
    }

    @Test
    @DisplayName("Retorna addition cuando existe para el id dado")
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

    @Test
    @DisplayName("Retorna nueva addition cuando no existe para el id dado")
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

    @Test
    @DisplayName("Retorna todas las additions cuando se invoca findAllService")
    void returnAllAdditionsWhenFindAllServiceIsCalled() {
        // Act
        additionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAllService retorna exactamente la lista del repositorio")
    void findAllServiceReturnsRepositoryList() {
        // Arrange
        List<Addition> expected = List.of(getAddition());
        when(repository.findAll()).thenReturn(expected);

        // Act
        List<Addition> out = additionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
        assertSame(expected, out);
    }

    @Test
    @DisplayName("Delete all additions cuando se provee una lista (incluye flush)")
    void deleteAllAdditionsWhenListIsProvided() {
        // Arrange
        List<Addition> list = List.of(getAddition(), new Addition());

        // Act
        additionService.deleteAllService(list);

        // Assert
        verify(repository, times(1)).deleteAll(list);
        verify(repository, times(1)).flush(); // cubrir flush()
    }

    @Test
    @DisplayName("Find by TaxDepreciation list retorna lo que devuelve el repositorio")
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

    @Test
    @DisplayName("Addition AllArgsConstructor setea todos los campos correctamente")
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