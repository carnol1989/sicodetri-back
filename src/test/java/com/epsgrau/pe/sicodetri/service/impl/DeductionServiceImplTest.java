package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DeductionServiceImplTest {

    @Mock
    private DeductionRepository repository;

    @InjectMocks
    private DeductionServiceImpl deductionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveService() {
        Deduction deduction = new Deduction();
        deductionService.saveService(deduction);
        verify(repository, times(1)).save(deduction);
    }

    @Test
    public void testUpdateService() {
        Deduction deduction = new Deduction();
        deductionService.updateService(deduction);
        verify(repository, times(1)).save(deduction);
    }

    @Test
    public void testDeleteByIdService() {
        Long id = 1L;
        deductionService.deleteByIdService(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void testFindByIdService_WhenPresent() {
        Long id = 1L;
        Deduction deduction = new Deduction();
        when(repository.findById(id)).thenReturn(Optional.of(deduction));
        Deduction result = deductionService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    public void testFindByIdService_WhenNotPresent() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        Deduction result = deductionService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    public void testFindAllService() {
        List<Deduction> deductions = List.of(new Deduction(), new Deduction());
        when(repository.findAll()).thenReturn(deductions);
        List<Deduction> result = deductionService.findAllService();
        verify(repository, times(1)).findAll();
        assertNotNull(result);
    }

}