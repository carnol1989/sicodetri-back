package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getFinancialDepreciation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.FinancialDepreciation;
import com.epsgrau.pe.sicodetri.repository.FinancialDepreciationRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    @DisplayName("Save financial depreciation when valid object is provided")
    public void saveFinancialDepreciationWhenValidObjectIsProvided() {
        // Arrange
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();

        // Act
        financialDepreciationService.saveService(financialDepreciation);

        // Assert
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    @DisplayName("Update financial depreciation when valid object is provided")
    public void updateFinancialDepreciationWhenValidObjectIsProvided() {
        // Arrange
        FinancialDepreciation financialDepreciation = new FinancialDepreciation();

        // Act
        financialDepreciationService.updateService(financialDepreciation);

        // Assert
        verify(repository, times(1)).save(financialDepreciation);
    }

    @Test
    @DisplayName("Delete financial depreciation when valid id is provided")
    public void deleteFinancialDepreciationWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        financialDepreciationService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Return financial depreciation when it exists for given id")
    public void returnFinancialDepreciationWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        FinancialDepreciation financialDepreciation = getFinancialDepreciation();
        when(repository.findById(id)).thenReturn(Optional.of(financialDepreciation));

        // Act
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(financialDepreciation, result);
    }

    @Test
    @DisplayName("Return new financial depreciation when none exists for given id")
    public void returnNewFinancialDepreciationWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        FinancialDepreciation result = financialDepreciationService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Return all financial depreciations when findAllService is called")
    public void returnAllFinancialDepreciationsWhenFindAllServiceIsCalled() {
        // Arrange
        List<FinancialDepreciation> financialDepreciations = List.of(new FinancialDepreciation(), new FinancialDepreciation());
        when(repository.findAll()).thenReturn(financialDepreciations);

        // Act
        List<FinancialDepreciation> result = financialDepreciationService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
        assertEquals(financialDepreciations, result);
    }

}