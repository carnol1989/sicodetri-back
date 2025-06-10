package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getTaxDepreciation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
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

}