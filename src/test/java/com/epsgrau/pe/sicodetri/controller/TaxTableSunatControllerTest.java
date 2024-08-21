package com.epsgrau.pe.sicodetri.controller;

import com.epsgrau.pe.sicodetri.entity.TaxTableSunat;
import com.epsgrau.pe.sicodetri.exception.GlobalExceptionHandler;
import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import com.epsgrau.pe.sicodetri.service.TaxTableSunatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaxTableSunatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaxTableSunatService service;

    @InjectMocks
    private TaxTableSunatController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())  // Configurar el manejador global de excepciones
                .build();
    }

    @Test
    @DisplayName("getTaxTableByIdController_ShouldReturnTaxTable")
    void getTaxTableByIdController_ShouldReturnTaxTable() throws Exception {
        // Arrange
        Long id = 1L;
        TaxTableSunat taxTableSunat = new TaxTableSunat();
        taxTableSunat.setId(id);
        when(service.findByIdService(id)).thenReturn(taxTableSunat);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tax-tables/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("getTaxTableByIdController_ShouldThrowResourceNotFoundException")
    void getTaxTableByIdController_ShouldThrowResourceNotFoundException() throws Exception {
        // Arrange
        Long id = 1L;
        // Crear una instancia de TaxTableSunat con ID nulo
        TaxTableSunat taxTableSunatWithNullId = new TaxTableSunat();
        // Sin establecer el ID para que sea nulo por defecto
        when(service.findByIdService(id)).thenReturn(taxTableSunatWithNullId);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tax-tables/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("ID no encontrado " + id, result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("getAllTaxesController_ShouldReturnAllTaxes")
    void getAllTaxesController_ShouldReturnAllTaxes() throws Exception {
        // Arrange
        TaxTableSunat taxTableSunat1 = new TaxTableSunat();
        TaxTableSunat taxTableSunat2 = new TaxTableSunat();
        List<TaxTableSunat> taxTableSunats = Arrays.asList(taxTableSunat1, taxTableSunat2);
        when(service.findAllService()).thenReturn(taxTableSunats);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tax-tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$").isArray());
    }

}