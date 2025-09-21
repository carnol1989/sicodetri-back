package com.epsgrau.pe.sicodetri.service.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epsgrau.pe.sicodetri.util.enums.ProjectionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for {@link ProjectionType}.
 *
 * <p>This suite verifies:</p>
 * <ul>
 *   <li><b>fromValue(String)</b>: mapping of valid codes (case-insensitive, trimmed) to the expected enum
 *       constants; and <i>error handling</i> for {@code null}, empty/blank, or unknown values with
 *       the exact messages:
 *       <ul>
 *         <li>{@code "Tipo de proyección no puede estar vacío"}</li>
 *         <li>{@code "Tipo de proyección desconocido: <value>"}</li>
 *       </ul>
 *   </li>
 *   <li><b>Accessors</b>: {@link ProjectionType#getCode()} and {@link ProjectionType#getDescription()} return
 *       the expected values for each constant.</li>
 *   <li><b>String form</b>: {@link ProjectionType#toString()} returns the same value as {@code getCode()}.</li>
 * </ul>
 *
 * <b>Class</b>: {@link ProjectionTypeTest}<br/>
 * <b>COPYRIGHT</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 25, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */

class ProjectionTypeTest {

  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_ADDITION", "temporary_addition", " TEMPORARY_ADDITION "})
  @DisplayName("return value when projection type is TEMPORARY_ADDITIONS")
  void returnValueWhenProjectionTypeIsTemporaryAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_ADDITIONS, pt);
  }

  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_DEDUCTION", "temporary_deduction", " TEMPORARY_DEDUCTION "})
  @DisplayName("return value when projection type is TEMPORARY_DEDUCTIONS")
  void returnValueWhenProjectionTypeIsTemporaryDeductions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_DEDUCTIONS, pt);
  }

  @ParameterizedTest
  @ValueSource(strings = {"PERMANENT_ADDITION", "permanent_addition", " PERMANENT_ADDITION "})
  @DisplayName("return value when projection type is PERMANENT_ADDITIONS")
  void returnValueWhenProjectionTypeIsPermanentAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.PERMANENT_ADDITIONS, pt);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("return IlegalArgumentException when ProjectionType is value Null or Blank")
  void returnIlegalArgumentExceptionWhenProjectionTypeIsValueNullOrBlank(String input) {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(input));
    assertEquals("Tipo de proyección no puede estar vacío", exception.getMessage());
  }

  @Test
  @DisplayName("return IlegalArgumentException when projection type is value unknown")
  void returnIlegalArgumentExceptionWhenProjectionTypeIsValueUnknown() {
    String unknown = "UNKNOWN";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(unknown));
    assertEquals("Tipo de proyección desconocido: " + unknown, exception.getMessage());
  }

  @Test
  @DisplayName("return description from projection type")
  void returnDescriptionFromProjectionType() {
    assertEquals("Deducción temporal", ProjectionType.TEMPORARY_DEDUCTIONS.getDescription());
    assertEquals("Adición temporal", ProjectionType.TEMPORARY_ADDITIONS.getDescription());
    assertEquals("Adición permanente", ProjectionType.PERMANENT_ADDITIONS.getDescription());
  }

  @Test
  @DisplayName("return code from projection type")
  void returnCodeFromProjectionType() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.getCode());
    assertEquals("TEMPORARY_ADDITION", ProjectionType.TEMPORARY_ADDITIONS.getCode());
    assertEquals("PERMANENT_ADDITION", ProjectionType.PERMANENT_ADDITIONS.getCode());
  }

  @Test
  @DisplayName("return string from projection type")
  void returnStringFromProjectionType() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.toString());
  }
}