package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getFixedAssetDepreciation;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.epsgrau.pe.sicodetri.entity.FixedAssetDepreciation;
import com.epsgrau.pe.sicodetri.repository.FixedAssetDepreciationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class FixedAssetDepreciationServiceImplTest {

    @Mock
    private FixedAssetDepreciationRepository repository;

    @InjectMocks
    private FixedAssetDepreciationServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("testSaveService")
    void testSaveService() {
        FixedAssetDepreciation fixedAssetDepreciation = getFixedAssetDepreciation();

        // Call the method under test
        service.saveService(fixedAssetDepreciation);

        // Verify interaction with repository
        verify(repository).save(fixedAssetDepreciation);
    }

    @Test
    @DisplayName("testUpdateService")
    void testUpdateService() {
        FixedAssetDepreciation fixedAssetDepreciation = getFixedAssetDepreciation();

        // Call the method under test
        service.updateService(fixedAssetDepreciation);

        // Verify interaction with repository
        verify(repository).save(fixedAssetDepreciation);
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
        FixedAssetDepreciation fixedAssetDepreciation = getFixedAssetDepreciation();

        // Prepare mock behavior
        when(repository.findById(1L)).thenReturn(Optional.of(fixedAssetDepreciation));

        // Call the method under test
        FixedAssetDepreciation result = service.findByIdService(1L);

        // Verify result
        assertEquals(fixedAssetDepreciation, result);

        // Verify interaction with repository
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("testFindByIdService - Not Found")
    void testFindByIdServiceNotFound() {
        // Prepare mock behavior
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // Call the method under test
        FixedAssetDepreciation result = service.findByIdService(1L);

        // Verify result
        assertNotNull(result);
        assertNull(result.getId());

        // Verify interaction with repository
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("testFindAllService")
    void testFindAllService() {
        FixedAssetDepreciation fixedAssetDepreciation = getFixedAssetDepreciation();

        // Prepare mock behavior
        when(repository.findAll()).thenReturn(Collections.singletonList(fixedAssetDepreciation));

        // Call the method under test
        List<FixedAssetDepreciation> result = service.findAllService();

        // Verify result
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(fixedAssetDepreciation, result.get(0));

        // Verify interaction with repository
        verify(repository).findAll();
    }

}