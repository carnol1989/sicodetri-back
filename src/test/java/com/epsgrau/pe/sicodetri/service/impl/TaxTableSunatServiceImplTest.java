package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.repository.TaxTableSunatRepository;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
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

class TaxTableSunatServiceImplTest {

    @Mock
    private TaxTableSunatRepository repository;

    @InjectMocks
    private TaxTableSunatServiceImpl service;

    public TaxTableSunatServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveService_ShouldSaveEntity")
    void saveService_ShouldSaveEntity() {
        // Arrange
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        // Act
        service.saveService(taxTableSunat);
        // Assert
        verify(repository, times(1)).save(taxTableSunat);
    }

    @Test
    @DisplayName("updateService_ShouldUpdateEntity")
    void updateService_ShouldUpdateEntity() {
        // Arrange
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        // Act
        service.updateService(taxTableSunat);
        // Assert
        verify(repository, times(1)).save(taxTableSunat);
    }

    @Test
    @DisplayName("deleteByIdService_ShouldDeleteEntity")
    void deleteByIdService_ShouldDeleteEntity() {
        // Arrange
        Long id = 1L;
        // Act
        service.deleteByIdService(id);
        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("findByIdService_ShouldReturnEntityIfExists")
    void findByIdService_ShouldReturnEntityIfExists() {
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
    @DisplayName("findByIdService_ShouldReturnNewEntityIfNotExists")
    void findByIdService_ShouldReturnNewEntityIfNotExists() {
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
    @DisplayName("findAllService_ShouldReturnAllEntities")
    void findAllService_ShouldReturnAllEntities() {
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

}