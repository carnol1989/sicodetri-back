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

/**
 * Unit tests for the {@link ReportStrategyFactory} class.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code getStrategy(String)} returns the correct {@link ReportStrategy}
 *       when the provided code matches exactly one registered strategy.</li>
 *   <li>{@code getStrategy(String)} throws {@link ResourceNotFoundException}
 *       when the provided code is not supported by any registered strategy.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link ReportStrategyFactoryTest}<br/>
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
 *     <li>Jun 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@DisplayName("ReportStrategyFactory Tests")
class ReportStrategyFactoryTest {

  private ReportStrategy taxBookReportStrategy;
  private ReportStrategy detailFinancialTaxDepreciationStrategy;
  private ReportStrategyFactory factory;

  /**
   * Test fixture initializer. Mocks two ReportStrategy implementations with codes "1" and "2",
   * then constructs the factory with those strategies.
   */
  @BeforeEach
  void setUp() {
    taxBookReportStrategy = mock(ReportStrategy.class);
    detailFinancialTaxDepreciationStrategy = mock(ReportStrategy.class);

    when(taxBookReportStrategy.getCode()).thenReturn("1");
    when(detailFinancialTaxDepreciationStrategy.getCode()).thenReturn("2");

    factory = new ReportStrategyFactory(List.of(taxBookReportStrategy, detailFinancialTaxDepreciationStrategy));
  }

  /**
   * Verifies that {@link ReportStrategyFactory#getStrategy(String)} returns the exact
   * strategy instance when the code is registered and unique.
   */
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

  /**
   * Verifies that {@link ReportStrategyFactory#getStrategy(String)} throws
   * {@link ResourceNotFoundException} with an appropriate message when the code
   * is not supported by any registered strategy.
   */
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