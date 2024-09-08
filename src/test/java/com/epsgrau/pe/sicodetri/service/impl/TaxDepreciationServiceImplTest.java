package com.epsgrau.pe.sicodetri.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epsgrau.pe.sicodetri.entity.TaxDepreciation;
import com.epsgrau.pe.sicodetri.repository.TaxDepreciationRepository;
import com.epsgrau.pe.sicodetri.util.DataTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;;

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
    @DisplayName("testSaveService")
    void testSaveService() {
        TaxDepreciation taxDepreciation = DataTest.getTaxDepreciation();

        // Call the method under test
        service.saveService(taxDepreciation);

        // Verify interaction with repository
        verify(repository).save(taxDepreciation);
    }

    @Test
    @DisplayName("testUpdateService")
    void testUpdateService() {
        TaxDepreciation taxDepreciation = DataTest.getTaxDepreciation();

        // Call the method under test
        service.updateService(taxDepreciation);

        // Verify interaction with repository
        verify(repository).save(taxDepreciation);
    }

    @Test
    @DisplayName("testDeleteByIdService")
    void testDeleteByIdService() {
        // Call the method under test
        service.deleteByIdService(1L);

        // Verify interaction with repository
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("testFindByIdService - Found")
    void testFindByIdServiceFound() {
        TaxDepreciation taxDepreciation = DataTest.getTaxDepreciation();

        // Prepare mock behavior
        when(repository.findById(1L)).thenReturn(Optional.of(taxDepreciation));

        // Call the method under test
        TaxDepreciation result = service.findByIdService(1L);

        // Verify result
        assertEquals(taxDepreciation, result);

        // Verify interaction with repository
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("testFindByIdService - Not Found")
    void testFindByIdServiceNotFound() {
        // Prepare mock behavior
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Call the method under test
        TaxDepreciation result = service.findByIdService(1L);

        // Verify result
        assertNotNull(result);
        assertNull(result.getId());

        // Verify interaction with repository
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("testFindAllService")
    void testFindAllService() {
        TaxDepreciation taxDepreciation = DataTest.getTaxDepreciation();

        // Prepare mock behavior
        when(repository.findAll()).thenReturn(Collections.singletonList(taxDepreciation));

        // Call the method under test
        List<TaxDepreciation> result = service.findAllService();

        // Verify result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(taxDepreciation, result.get(0));

        // Verify interaction with repository
        verify(repository).findAll();
    }

}