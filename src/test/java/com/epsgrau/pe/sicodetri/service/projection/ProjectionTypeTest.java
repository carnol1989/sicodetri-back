package com.epsgrau.pe.sicodetri.service.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ProjectionTypeTest {

  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_DEDUCTION", "temporary_deduction", " TEMPORARY_DEDUCTION "})
  @DisplayName("fromValue debería devolver TEMPORARY_DEDUCTIONS para códigos válidos")
  void testFromValueTemporaryDeductions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_DEDUCTIONS, pt);
  }

  @ParameterizedTest
  @ValueSource(strings = {"TEMPORARY_ADDITION", "temporary_addition", " TEMPORARY_ADDITION "})
  @DisplayName("fromValue debería devolver TEMPORARY_ADDITIONS para códigos válidos")
  void testFromValueTemporaryAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.TEMPORARY_ADDITIONS, pt);
  }

  @ParameterizedTest
  @ValueSource(strings = {"PERMANENT_ADDITIONS", "permanent_additions", " PERMANENT_ADDITIONS "})
  @DisplayName("fromValue debería devolver PERMANENT_ADDITIONS para códigos válidos")
  void testFromValuePermanentAdditions(String input) {
    ProjectionType pt = ProjectionType.fromValue(input);
    assertEquals(ProjectionType.PERMANENT_ADDITIONS, pt);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" "})
  @DisplayName("fromValue debería lanzar IllegalArgumentException para null, vacío o en blanco")
  void testFromValueNullOrBlank(String input) {
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(input));
    assertEquals("Tipo de proyección no puede estar vacío", exception.getMessage());
  }

  @Test
  @DisplayName("fromValue debería lanzar IllegalArgumentException para código desconocido")
  void testFromValueUnknown() {
    String unknown = "UNKNOWN";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> ProjectionType.fromValue(unknown));
    assertEquals("Tipo de proyección desconocido: " + unknown, exception.getMessage());
  }

  @Test
  @DisplayName("getCode debería devolver el código correcto")
  void testGetCode() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.getCode());
    assertEquals("TEMPORARY_ADDITION", ProjectionType.TEMPORARY_ADDITIONS.getCode());
    assertEquals("PERMANENT_ADDITIONS", ProjectionType.PERMANENT_ADDITIONS.getCode());
  }

  @Test
  @DisplayName("getDescription debería devolver la descripción correcta")
  void testGetDescription() {
    assertEquals("Deducción temporal", ProjectionType.TEMPORARY_DEDUCTIONS.getDescription());
    assertEquals("Adición temporal", ProjectionType.TEMPORARY_ADDITIONS.getDescription());
    assertEquals("Adición permanente", ProjectionType.PERMANENT_ADDITIONS.getDescription());
  }

  @Test
  @DisplayName("toString debería devolver el código")
  void testToString() {
    assertEquals("TEMPORARY_DEDUCTION", ProjectionType.TEMPORARY_DEDUCTIONS.toString());
  }
}