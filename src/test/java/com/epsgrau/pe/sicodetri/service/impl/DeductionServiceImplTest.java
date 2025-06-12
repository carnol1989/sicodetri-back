package com.epsgrau.pe.sicodetri.service.impl;

import static com.epsgrau.pe.sicodetri.util.DataTest.getDeduction;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.entity.Deduction;
import com.epsgrau.pe.sicodetri.repository.DeductionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * This class test for DeductionServiceImpl. <br/>
 * <b>Class</b>: {@link DeductionServiceImplTest}<br/>
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
    @DisplayName("Save deduction when valid deduction is provided")
    public void saveDeductionWhenValidDeductionIsProvided() {
        // Arrange
        Deduction deduction = new Deduction();

        // Act
        deductionService.saveService(deduction);

        // Assert
        verify(repository, times(1)).save(deduction);
    }

    @Test
    @DisplayName("Update deduction when valid deduction is provided")
    public void updateDeductionWhenValidDeductionIsProvided() {
        // Arrange
        Deduction deduction = new Deduction();

        // Act
        deductionService.updateService(deduction);

        // Assert
        verify(repository, times(1)).save(deduction);
    }

    @Test
    @DisplayName("Delete deduction when valid id is provided")
    public void deleteDeductionWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        deductionService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Return deduction when it exists for given id")
    public void returnDeductionWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        Deduction deduction = getDeduction();
        when(repository.findById(id)).thenReturn(Optional.of(deduction));

        // Act
        Deduction result = deductionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(deduction, result);
    }

    @Test
    @DisplayName("Return new deduction when none exists for given id")
    public void returnNewDeductionWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Deduction result = deductionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Return all deductions when findAllService is called")
    public void returnAllDeductionsWhenFindAllServiceIsCalled() {
        // Arrange
        List<Deduction> deductions = List.of(new Deduction(), new Deduction());
        when(repository.findAll()).thenReturn(deductions);

        // Act
        List<Deduction> result = deductionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
        assertNotNull(result);
    }

}