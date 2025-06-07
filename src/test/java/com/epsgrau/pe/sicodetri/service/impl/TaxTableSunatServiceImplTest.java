package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

}