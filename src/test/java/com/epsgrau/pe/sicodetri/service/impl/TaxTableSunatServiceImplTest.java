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
 * This class test for TaxTableSunatServiceImpl. <br/>
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
 *     <li>Ago 18, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class TaxTableSunatServiceImplTest {

    @Mock
    private TaxTableSunatRepository repository;

    @InjectMocks
    private TaxTableSunatServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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

    @Test
    @DisplayName("deleteAllService invokes repository.deleteAll")
    void deleteAllServiceInvokesDeleteAll() {
        List<TaxTableSunat> list = Arrays.asList(new TaxTableSunat(), new TaxTableSunat());
        service.deleteAllService(list);
        verify(repository).deleteAll(list);
    }

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

    @Test
    @DisplayName("Return TaxTableSunat when findByAccountingAccountService is called with existing account")
    public void returnTaxTableSunatWhenFindByAccountingAccountServiceCalledWithExistingAccount() {
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

    @Test
    @DisplayName("Return null when findByAccountingAccountService is called with non-existing account")
    public void returnNullWhenFindByAccountingAccountServiceCalledWithNonExistingAccount() {
        // Arrange
        String accountingAccount = "123456";
        when(repository.findByAccountingAccount(accountingAccount)).thenReturn(null);

        // Act
        TaxTableSunat result = service.findByAccountigAccountService(accountingAccount);

        // Assert
        assertNull(result);
        verify(repository, times(1)).findByAccountingAccount(accountingAccount);
    }

    @Test
    @DisplayName("AllArgsConstructor, getters and toString()")
    void allArgsConstructorAndGetters() {
        Long id = 9L;
        String accAccount = "332";
        String desc       = "Construcciones";
        Integer rate      = 5;
        Integer lifeYears = 20;
        String status     = "S";

        TaxTableSunat t = new TaxTableSunat(
            id,
            accAccount,
            desc,
            rate,
            lifeYears,
            status
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

    @Test
    @DisplayName("equals & hashCode")
    void equalsAndHashCode() {
        TaxTableSunat a = new TaxTableSunat(1L, "X", "D", 1, 1, "Y");
        TaxTableSunat b = new TaxTableSunat(1L, "X", "D", 1, 1, "Y");
        TaxTableSunat c = new TaxTableSunat(2L, "Z", "D2", 2, 2, "N");

        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));
    }

}