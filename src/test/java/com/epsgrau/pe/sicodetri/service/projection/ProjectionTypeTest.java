package com.epsgrau.pe.sicodetri.service.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for the {@link ProjectionType} enum.
 *
 * <p>
 * Verifies that {@code fromValue} correctly maps valid string codes (in various cases and with whitespace)
 * to their corresponding {@code ProjectionType} values, and throws appropriate exceptions for null,
 * empty, blank, or unknown codes. Also tests the {@code getCode}, {@code getDescription}, and
 * {@code toString} methods for each enum constant.
 * </p>
 *
 * <b>Class</b>: {@link ProjectionTypeTest}<br/>
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
 *     <li>Jun 25, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class ProjectionTypeTest {
  /**
   * Verifies that {@link ProjectionType#fromValue(String)} returns
   * {@link ProjectionType#TEMPORARY_ADDITIONS} for various valid input codes,
   * ignoring case and trimming whitespace.
   *
   * @param input the code string to convert
   */
  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_ADDITION", "temporary_addition", " TEMPORARY_ADDITION "})
  @DisplayName("return value when projection type is TEMPORARY_ADDITIONS")
  void returnValueWhenProjectionTypeIsTemporaryAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_ADDITIONS, pt);
  }


  /**
   * Verifies that {@link ProjectionType#fromValue(String)} returns
   * {@link ProjectionType#TEMPORARY_DEDUCTIONS} for various valid input codes,
   * ignoring case and trimming whitespace.
   *
   * @param input the code string to convert
   */
  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_DEDUCTION", "temporary_deduction", " TEMPORARY_DEDUCTION "})
  @DisplayName("return value when projection type is TEMPORARY_DEDUCTIONS")
  void returnValueWhenProjectionTypeIsTemporaryDeductions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_DEDUCTIONS, pt);
  }

  /**
   * Verifies that {@link ProjectionType#fromValue(String)} returns
   * {@link ProjectionType#PERMANENT_ADDITIONS} for various valid input codes,
   * ignoring case and trimming whitespace.
   *
   * @param input the code string to convert
   */
  @ParameterizedTest
  @ValueSource(strings = {"PERMANENT_ADDITIONS", "permanent_additions", " PERMANENT_ADDITIONS "})
  @DisplayName("return value when projection type is PERMANENT_ADDITIONS")
  void returnValueWhenProjectionTypeIsPermanentAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.PERMANENT_ADDITIONS, pt);
  }

  /**
   * Verifies that {@link ProjectionType#fromValue(String)} throws
   * {@link IllegalArgumentException} when given null, empty, or blank strings.
   *
   * @param input the code string (null, empty, or blank)
   */
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("return IlegalArgumentException when ProjectionType is value Null or Blank")
  void returnIlegalArgumentExceptionWhenProjectionTypeIsValueNullOrBlank(String input) {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(input));
    assertEquals("Tipo de proyección no puede estar vacío", exception.getMessage());
  }

  /**
   * Verifies that {@link ProjectionType#fromValue(String)} throws
   * {@link IllegalArgumentException} for an unknown code.
   */
  @Test
  @DisplayName("return IlegalArgumentException when projection type is value unknown")
  void returnIlegalArgumentExceptionWhenProjectionTypeIsValueUnknown() {
    String unknown = "UNKNOWN";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(unknown));
    assertEquals("Tipo de proyección desconocido: " + unknown, exception.getMessage());
  }

  /**
   * Verifies that {@link ProjectionType#getDescription()} returns the correct
   * human-readable description for each enum constant.
   */
  @Test
  @DisplayName("return description from projection type")
  void returnDescriptionFromProjectionType() {
    assertEquals("Deducción temporal", ProjectionType.TEMPORARY_DEDUCTIONS.getDescription());
    assertEquals("Adición temporal", ProjectionType.TEMPORARY_ADDITIONS.getDescription());
    assertEquals("Adición permanente", ProjectionType.PERMANENT_ADDITIONS.getDescription());
  }

  /**
   * Verifies that {@link ProjectionType#getCode()} returns the correct code
   * for each enum constant.
   */
  @Test
  @DisplayName("return code from projection type")
  void returnCodeFromProjectionType() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.getCode());
    assertEquals("TEMPORARY_ADDITION", ProjectionType.TEMPORARY_ADDITIONS.getCode());
    assertEquals("PERMANENT_ADDITIONS", ProjectionType.PERMANENT_ADDITIONS.getCode());
  }

  /**
   * Verifies that {@link ProjectionType#toString()} returns the enum constant's code.
   */
  @Test
  @DisplayName("return string from projection type")
  void returnStringFromProjectionType() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.toString());
  }
}