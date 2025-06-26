package com.epsgrau.pe.sicodetri.util;

import static com.epsgrau.pe.sicodetri.util.Constants.D_VAL_ZERO;
import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class FunctionsTest {

  @Test
  @DisplayName("Return date with format correct when convertDateToLocalDateString")
  void returnDateWithFormatCorrectWhenConvertDateToLocalDateString() {
    LocalDate ld = LocalDate.of(2025, 6, 25);
    Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    String result = Functions.convertDateToLocalDateString(date, "dd/MM/yyyy");
    assertEquals("2025-06-25", result);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return default local date when input null or blank in parseDate")
  void returnDefaultLocalDateWhenInputNullOrBlankInParseDate(String input) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate result = Functions.parseDate(input, formatter, 1, "fecha");
    assertEquals(LOCAL_DATE_DEFAULT, result);
  }

  @Test
  @DisplayName("Return parsed local date when parseDate with valid date string")
  void returnParsedLocalDateWhenParseDateWithValidDateString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate expected = LocalDate.of(2025, 6, 25);
    LocalDate result = Functions.parseDate("25/06/2025", formatter, 1, "fecha");
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Throw IllegalArgumentException when parseDate with invalid format")
  void throwIllegalArgumentExceptionWhenParseDateWithInvalidFormat() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseDate("2025-06-25", formatter, 2, "fecha"));
    assertEquals("Formato de fecha inválido en 'fecha' (fila 2): '2025-06-25'", ex.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return zero when parseBigDecimal input null or blank")
  void returnZeroWhenParseBigDecimalInputNullOrBlank(String input) {
    BigDecimal result = Functions.parseBigDecimal(input, 1, "campo");
    assertEquals(BigDecimal.ZERO, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"1234.56", "1,234.56", "- 123", "0", "-0"})
  @DisplayName("Return BigDecimal correct when parseBigDecimal with valid input")
  void returnBigDecimalCorrectWhenParseBigDecimalWithValidInput(String input) {
    BigDecimal result = Functions.parseBigDecimal(input, 1, "campo");
    BigDecimal expected = new BigDecimal(input.replace(",", "").replaceAll("-\\s+(\\d+)", "-$1"));
    assertEquals(0, expected.compareTo(result));
  }

  @Test
  @DisplayName("Throw IllegalArgumentException when parseBigDecimal with invalid input")
  void throwIllegalArgumentExceptionWhenParseBigDecimalWithInvalidInput() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseBigDecimal("abc", 3, "monto"));
    assertEquals("Número inválido en 'monto' (fila 3): 'abc'", ex.getMessage());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return zero when parseDouble input null or blank")
  void returnZeroWhenParseDoubleInputNullOrBlank(String input) {
    Double result = Functions.parseDouble(input, 1, "campo");
    assertEquals(D_VAL_ZERO, result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"123.45", "1,234.56", "- 123.4", "0", "-0.0"})
  @DisplayName("Return double correct when parseDouble with valid input")
  void returnDoubleCorrectWhenParseDoubleWithValidInput(String input) {
    Double result = Functions.parseDouble(input, 1, "campo");
    Double expected = Double.parseDouble(input.replace(",", "").replaceAll("-\\s+(\\d+(\\.\\d+)?)", "-$1"));
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Throw IllegalArgumentException when parseDouble with invalid input")
  void throwIllegalArgumentExceptionWhenParseDoubleWithInvalidInput() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseDouble("xyz", 4, "monto"));
    assertEquals("Número double inválido en 'monto' (fila 4): 'xyz'", ex.getMessage());
  }

  @Test
  @DisplayName("Return blank when defaultString input null")
  void returnBlankWhenDefaultStringInputNull() {
    assertEquals(_BLANK, Functions.defaultString(null));
  }

  @Test
  @DisplayName("Return original string when defaultString input non-null")
  void returnOriginalStringWhenDefaultStringInputNonNull() {
    String val = "texto";
    assertEquals(val, Functions.defaultString(val));
  }

  @Test
  @DisplayName("Return value when getStringOrDefault result not null")
  void returnValueWhenGetStringOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("col")).thenReturn("valor");
    assertEquals("valor", Functions.getStringOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return blank when getStringOrDefault result null")
  void returnBlankWhenGetStringOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("col")).thenReturn(null);
    assertEquals(_BLANK, Functions.getStringOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return value when getBigDecimalOrDefault result not null")
  void returnValueWhenGetBigDecimalOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    BigDecimal val = new BigDecimal("123.45");
    when(rs.getBigDecimal("col")).thenReturn(val);
    assertEquals(val, Functions.getBigDecimalOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return zero when getBigDecimalOrDefault result null")
  void returnZeroWhenGetBigDecimalOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getBigDecimal("col")).thenReturn(null);
    assertEquals(BigDecimal.ZERO, Functions.getBigDecimalOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return value when getIntegerOrDefault result not null")
  void returnValueWhenGetIntegerOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getInt("col")).thenReturn(5);
    assertEquals(Integer.valueOf(5), Functions.getIntegerOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return date when getDateOrDefault result not null")
  void returnDateWhenGetDateOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    java.sql.Date sqlDate = java.sql.Date.valueOf("2025-06-25");
    when(rs.getDate("col")).thenReturn(sqlDate);
    assertEquals(LocalDate.of(2025, 6, 25), Functions.getDateOrDefault(rs, "col"));
  }

  @Test
  @DisplayName("Return default local date when getDateOrDefault result null")
  void returnDefaultLocalDateWhenGetDateOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getDate("col")).thenReturn(null);
    assertEquals(LOCAL_DATE_DEFAULT, Functions.getDateOrDefault(rs, "col"));
  }
}