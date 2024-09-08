package com.epsgrau.pe.sicodetri.service.impl;

import com.epsgrau.pe.sicodetri.entity.Addition;
import com.epsgrau.pe.sicodetri.repository.AdditionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
    public void testSaveService() {
        Addition addition = new Addition();
        additionService.saveService(addition);
        verify(repository, times(1)).save(addition);
    }

    @Test
    public void testUpdateService() {
        Addition addition = new Addition();
        additionService.updateService(addition);
        verify(repository, times(1)).save(addition);
    }

    @Test
    public void testDeleteByIdService() {
        Long id = 1L;
        additionService.deleteByIdService(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void testFindByIdService_WhenPresent() {
        Long id = 1L;
        Addition addition = new Addition();
        when(repository.findById(id)).thenReturn(Optional.of(addition));
        Addition result = additionService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertSame(addition, result);
    }

    @Test
    public void testFindByIdService_WhenNotPresent() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());
        Addition result = additionService.findByIdService(id);
        verify(repository, times(1)).findById(id);
        assertNotNull(result);
    }

    @Test
    public void testFindAllService() {
        additionService.findAllService();
        verify(repository, times(1)).findAll();
    }

}