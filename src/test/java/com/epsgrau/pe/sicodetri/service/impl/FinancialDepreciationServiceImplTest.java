package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * This class test for FinancialDepreciationServiceImpl. <br/>
 * <b>Class</b>: {@link FinancialDepreciationServiceImplTest}<br/>
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
 *     <li>Ago 27, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class FinancialDepreciationServiceImplTest {

    @Mock
    private FinancialDepreciationRepository repository;

    @InjectMocks
    private FinancialDepreciationServiceImpl financialDepreciationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveService() {
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        financialDepreciationService.saveService(financialDepreciation);
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    public void testUpdateService() {
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        financialDepreciationService.updateService(financialDepreciation);
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    public void testDeleteByIdService() {
        Long id = 1L;
        financialDepreciationService.deleteByIdService(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void testFindByIdService_WhenPresent() {
        Long id = 1L;
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();
        when(repository.findById(id)).thenReturn(Optional.of(financialDepreciation));
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    public void testFindByIdService_WhenNotPresent() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    public void testFindAllService() {
        List<FinancialDepreciation> financialDepreciations = List.of(new FinancialDepreciation(), new FinancialDepreciation());
        when(repository.findAll()).thenReturn(financialDepreciations);
        List<FinancialDepreciation> result = financialDepreciationService.findAllService();
        verify(repository, times(1)).findAll();
        assertNotNull(result);
    }

}