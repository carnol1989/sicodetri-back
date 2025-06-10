package com.epsgrau.pe.sicodetri.service.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.exception.ResourceNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ReportStrategyFactory Tests")
class ReportStrategyFactoryTest {

  private ReportStrategy taxBookReportStrategy;
  private ReportStrategy detailFinancialTaxDepreciationStrategy;
  private ReportStrategyFactory factory;

  @BeforeEach
  void setUp() {
    taxBookReportStrategy = mock(ReportStrategy.class);
    detailFinancialTaxDepreciationStrategy = mock(ReportStrategy.class);

    when(taxBookReportStrategy.getCode()).thenReturn("1");
    when(detailFinancialTaxDepreciationStrategy.getCode()).thenReturn("2");

    factory = new ReportStrategyFactory(List.of(taxBookReportStrategy, detailFinancialTaxDepreciationStrategy));
  }

  @Test
  @DisplayName("Return reportStrategy when code exists in strategies and be unique")
  void returnReportStrategyWhenCodeExistsAndBeUnique() {
    // Act
    ReportStrategy resultA = factory.getStrategy("1");
    ReportStrategy resultB = factory.getStrategy("2");

    // Assert
    assertSame(taxBookReportStrategy, resultA, "Debe devolver la estrategia para el código '1'");
    assertSame(detailFinancialTaxDepreciationStrategy, resultB, "Debe devolver la estrategia para el código '2'");
  }

  @Test
  @DisplayName("Throw ResourceNotFoundException when code is not supported")
  void throwResourceNotFoundExceptionWhenCodeIsNotSupported() {
    // Arrange
    String invalidCode = "Z";

    // Act & Assert
    ResourceNotFoundException ex = assertThrows(
        ResourceNotFoundException.class,
        () -> factory.getStrategy(invalidCode),
        "Si el código no está en el mapa, debe lanzarse ResourceNotFoundException"
    );
    assertEquals(
        "Tipo de reporte no soportado para opción: " + invalidCode,
        ex.getMessage(),
        "El mensaje de la excepción debe contener el código no soportado"
    );
  }

}