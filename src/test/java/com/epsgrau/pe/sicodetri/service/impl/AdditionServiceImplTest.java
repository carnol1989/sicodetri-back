package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static com.epsgrau.pe.sicodetri.util.DataTest.getAddition;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * This class test for AdditionServiceImpl. <br/>
 * <b>Class</b>: {@link AdditionServiceImplTest}<br/>
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
class AdditionServiceImplTest {

    @Mock
    private AdditionRepository repository;

    @InjectMocks
    private AdditionServiceImpl additionService;

    public AdditionServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save addition when valid addition is provided")
    public void saveAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.saveService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    @Test
    @DisplayName("Update addition when valid addition is provided")
    public void updateAdditionWhenValidAdditionIsProvided() {
        // Arrange
        Addition addition = new Addition();

        // Act
        additionService.updateService(addition);

        // Assert
        verify(repository, times(1)).save(addition);
    }

    @Test
    @DisplayName("Delete addition when valid id is provided")
    public void deleteAdditionWhenValidIdIsProvided() {
        // Arrange
        Long id = 1L;

        // Act
        additionService.deleteByIdService(id);

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Return addition when it exists for given id")
    public void returnAdditionWhenItExistsForGivenId() {
        // Arrange
        Long id = 1L;
        Addition addition = getAddition();
        when(repository.findById(id)).thenReturn(Optional.of(addition));

        // Act
        Addition result = additionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertSame(addition, result);
    }

    @Test
    @DisplayName("Return new addition when none exists for given id")
    public void returnNewAdditionWhenNoneExistsForGivenId() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act
        Addition result = additionService.findByIdService(id);

        // Assert
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Return all additions when findAllService is called")
    public void returnAllAdditionsWhenFindAllServiceIsCalled() {
        // Act
        additionService.findAllService();

        // Assert
        verify(repository, times(1)).findAll();
    }

}