package com.epsgrau.pe.sicodetri.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
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
 * Unit tests for {@link TaxTableSunatServiceImpl}.
 *
 * <p>
 * Verifies the behavior of the TaxTableSunatServiceImpl, ensuring that it correctly
 * delegates CRUD operations to the underlying {@link TaxTableSunatRepository}, handles
 * lookup by accounting account, and properly implements its constructors, getters,
 * setters, equals, and hashCode methods.
 * </p>
 *
 * <b>Class</b>: {@link TaxTableSunatServiceImplTest}<br/>
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
class TaxTableSunatServiceImplTest {

    @Mock
    private TaxTableSunatRepository repository;

    @InjectMocks
    private TaxTableSunatServiceImpl service;

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Verifies that saveService returns the exact entity returned by the repository.
     */
    @Test
    @DisplayName("saveService returns the saved entity")
    void saveServiceReturnsSavedEntity() {
        // Arrange
        TaxTableSunat input = new TaxTableSunat();
        TaxTableSunat saved = new TaxTableSunat();
        when(repository.save(input)).thenReturn(saved);

        // Act
        TaxTableSunat result = service.saveService(input);

        // Assert
        verify(repository).save(input);
        assertSame(saved, result, "Debe devolver exactamente el objeto retornado por el repositorio");
    }

    /**
     * Verifies that saveAllService delegates saving of a list to repository.saveAll.
     */
    @Test
    @DisplayName("saveAllService invokes repository.saveAll with given list")
    void saveAllServiceInvokesSaveAll() {
        // Arrange
        List<TaxTableSunat> list = Arrays.asList(new TaxTableSunat(), new TaxTableSunat());

        // Act
        service.saveAllService(list);

        // Assert
        verify(repository).saveAll(list);
    }

    /**
     * Verifies that findAllService returns an empty list when repository is empty.
     */
    @Test
    @DisplayName("findAllService returns empty list when repository is empty")
    void findAllServiceReturnsEmptyListWhenNoEntities() {
        // Arrange
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<TaxTableSunat> result = service.findAllService();

        // Assert
        verify(repository).findAll();
        assertNotNull(result, "No debe regresar null aunque no haya datos");
        assertTrue(result.isEmpty(), "La lista debe venir vacía si no hay entidades");
    }

    /**
     * Verifies that saveService persists a valid TaxTableSunat entity.
     */
    @Test
    @DisplayName("Save TaxTableSunat when valid entity is provided")
    void saveTaxTableSunatWhenValidEntityIsProvided() {
        // Arrange
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        // Act
        service.saveService(taxTableSunat);
        // Assert
        verify(repository, times(1)).save(taxTableSunat);
    }

    /**
     * Verifies that updateService persists updates to a TaxTableSunat entity.
     */
    @Test
    @DisplayName("Update TaxTableSunat when valid entity is provided")
    void updateTaxTableSunatWhenValidEntityIsProvided() {
        // Arrange
        TaxTableSunat taxTableSunat = new TaxTableSunat();

        // Act
        service.updateService(taxTableSunat);

        // Assert
        verify(repository, times(1)).save(taxTableSunat);
    }

    /**
     * Verifies that deleteByIdService removes the entity with the given ID.
     */
    @Test
    @DisplayName("Delete TaxTableSunat when valid id is provided")
    void deleteTaxTableSunatWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    /**
     * Verifies that deleteAllService delegates deletion of a list to repository.deleteAll.
     */
    @Test
    @DisplayName("deleteAllService invokes repository.deleteAll")
    void deleteAllServiceInvokesDeleteAll() {
        List<TaxTableSunat> list = Arrays.asList(new TaxTableSunat(), new TaxTableSunat());
        service.deleteAllService(list);
        verify(repository).deleteAll(list);
    }

    /**
     * Verifies that findByIdService returns an existing entity when present.
     */
    @Test
    @DisplayName("Return existing TaxTableSunat when findByIdService is called with existing id")
    void returnExistingTaxTableSunatWhenFindByIdServiceCalledWithExistingId() {
        // Arrange
        Long id = 1L;
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        when(repository.findById(id)).thenReturn(Optional.of(taxTableSunat));

        // Act
        TaxTableSunat result = service.findByIdService(id);

        // Assert
        assertNotNull(result);
        assertEquals(taxTableSunat, result);
    }

    /**
     * Verifies that findByIdService returns a new default entity when the ID is not found.
     */
    @Test
    @DisplayName("Return new TaxTableSunat when findByIdService is called with non-existing id")
    void returnNewTaxTableSunatWhenFindByIdServiceCalledWithNonExistingId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        TaxTableSunat result = service.findByIdService(id);

        // Assert
        assertNotNull(result);
        assertEquals(new TaxTableSunat(), result);
    }

    /**
     * Verifies that findAllService returns all entities when repository has data.
     */
    @Test
    @DisplayName("Return all TaxTableSunat entities when findAllService is called")
    void returnAllTaxTableSunatEntitiesWhenFindAllServiceCalled() {
        // Arrange
        TaxTableSunat taxTableSunat1 = new TaxTableSunat();
        TaxTableSunat taxTableSunat2 = new TaxTableSunat();
        when(repository.findAll()).thenReturn(Arrays.asList(taxTableSunat1, taxTableSunat2));

        // Act
        List<TaxTableSunat> result = service.findAllService();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Verifies that findByAccountigAccountService returns the entity for an existing account.
     */
    @Test
    @DisplayName("Return TaxTableSunat when findByAccountingAccountService is called with existing account")
    void returnTaxTableSunatWhenFindByAccountingAccountServiceCalledWithExistingAccount() {
        // Arrange
        String accountingAccount = "123456";
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        taxTableSunat.setAccountingAccount(accountingAccount);
        when(repository.findByAccountingAccount(accountingAccount)).thenReturn(taxTableSunat);

        // Act
        TaxTableSunat result = service.findByAccountigAccountService(accountingAccount);

        // Assert
        assertEquals(taxTableSunat, result);
        verify(repository, times(1)).findByAccountingAccount(accountingAccount);
    }

    /**
     * Verifies that findByAccountigAccountService returns null when no entity matches the account.
     */
    @Test
    @DisplayName("Return null when findByAccountingAccountService is called with non-existing account")
    void returnNullWhenFindByAccountingAccountServiceCalledWithNonExistingAccount() {
        // Arrange
        String accountingAccount = "123456";
        when(repository.findByAccountingAccount(accountingAccount)).thenReturn(null);

        // Act
        TaxTableSunat result = service.findByAccountigAccountService(accountingAccount);

        // Assert
        assertNull(result);
        verify(repository, times(1)).findByAccountingAccount(accountingAccount);
    }

    /**
     * Verifies that all-args constructor sets fields correctly, and toString includes key values.
     */
    @Test
    @DisplayName("AllArgsConstructor, getters and toString()")
    void allArgsConstructorAndGetters() {
        Long id = 9L;
        String accAccount = "332";
        String desc       = "Construcciones";
        Integer rate      = 5;
        Integer lifeYears = 20;
        String status     = "S";
        String addedBy = "TEST";
        LocalDateTime additionDate = LocalDateTime.now();

        TaxTableSunat t = new TaxTableSunat(
            id,
            accAccount,
            desc,
            rate,
            lifeYears,
            status,
            addedBy,
            additionDate,
            null,
            null
        );

        // getters
        assertEquals(id,            t.getId());
        assertEquals(accAccount,    t.getAccountingAccount());
        assertEquals(desc,          t.getDescription());
        assertEquals(rate,          t.getAnnualRate());
        assertEquals(lifeYears,     t.getUsefulLifeYears());
        assertEquals(status,        t.getStatus());

        // toString contains some key pieces
        String s = t.toString();
        assertTrue(s.contains("id=" + id));
        assertTrue(s.contains("accountingAccount=" + accAccount));
        assertTrue(s.contains("annualRate=" + rate));
    }

    /**
     * Verifies that no-args constructor and setters correctly populate fields.
     */
    @Test
    @DisplayName("No-args constructor and setters")
    void noArgsConstructorAndSetters() {
        TaxTableSunat t = new TaxTableSunat();

        t.setId(7L);
        t.setAccountingAccount("777");
        t.setDescription("Desc777");
        t.setAnnualRate(7);
        t.setUsefulLifeYears(7);
        t.setStatus("N");

        assertEquals(7L,        t.getId());
        assertEquals("777",     t.getAccountingAccount());
        assertEquals("Desc777", t.getDescription());
        assertEquals(7,         t.getAnnualRate());
        assertEquals(7,         t.getUsefulLifeYears());
        assertEquals("N",       t.getStatus());
    }

    /**
     * Verifies equals() and hashCode() implementations for logical equality.
     */
    @Test
    @DisplayName("equals & hashCode")
    void equalsAndHashCode() {
        TaxTableSunat a = new TaxTableSunat(1L, "X", "D", 1, 1,
            "Y", null, null, null, null);
        TaxTableSunat b = new TaxTableSunat(1L, "X", "D", 1, 1,
            "Y", null, null, null, null);
        TaxTableSunat c = new TaxTableSunat(2L, "Z", "D2", 2, 2,
            "N", null, null, null, null);

        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
    }

}