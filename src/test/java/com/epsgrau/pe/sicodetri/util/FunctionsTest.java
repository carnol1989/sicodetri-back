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

/**
 * Unit tests for the {@link Functions} utility class.
 *
 * <p>
 * Verifies that:
 * <ul>
 *   <li>{@code convertDateToLocalDateString} formats Date objects correctly.</li>
 *   <li>{@code parseDate} handles null, blank, valid, and invalid inputs with correct defaults and exceptions.</li>
 *   <li>{@code parseBigDecimal} and {@code parseDouble} parse numeric strings with various formats
 *       and handle invalid inputs by throwing {@link IllegalArgumentException}.</li>
 *   <li>{@code defaultString} returns blank or original string as appropriate.</li>
 *   <li>ResultSet helpers {@code getStringOrDefault}, {@code getBigDecimalOrDefault},
 *       {@code getIntegerOrDefault}, and {@code getDateOrDefault} return defaults when the
 *       database value is null, or the actual value when present.</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link FunctionsTest}<br/>
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
class FunctionsTest {

  /**
   * Tests that a {@link Date} is converted to a LocalDate string using the specified format,
   * defaulting to ISO date format if the provided format does not match.
   */
  @Test
  @DisplayName("Return date with format correct when convertDateToLocalDateString")
  void returnDateWithFormatCorrectWhenConvertDateToLocalDateString() {
    LocalDate ld = LocalDate.of(2025, 6, 25);
    Date date = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    String result = Functions.convertDateToLocalDateString(date, "dd/MM/yyyy");
    assertEquals("2025-06-25", result);
  }

  /**
   * Tests that {@code parseDate} returns the default {@link LocalDate} when the input string
   * is null or blank.
   */
  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return default local date when input null or blank in parseDate")
  void returnDefaultLocalDateWhenInputNullOrBlankInParseDate(String input) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate result = Functions.parseDate(input, formatter, 1, "fecha");
    assertEquals(LOCAL_DATE_DEFAULT, result);
  }

  /**
   * Tests that {@code parseDate} correctly parses a valid date string.
   */
  @Test
  @DisplayName("Return parsed local date when parseDate with valid date string")
  void returnParsedLocalDateWhenParseDateWithValidDateString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate expected = LocalDate.of(2025, 6, 25);
    LocalDate result = Functions.parseDate("25/06/2025", formatter, 1, "fecha");
    assertEquals(expected, result);
  }

  /**
   * Tests that {@code parseDate} throws {@link IllegalArgumentException} when the date string
   * does not match the provided formatter.
   */
  @Test
  @DisplayName("Throw IllegalArgumentException when parseDate with invalid format")
  void throwIllegalArgumentExceptionWhenParseDateWithInvalidFormat() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseDate("2025-06-25", formatter, 2, "fecha"));
    assertEquals("Formato de fecha inválido en 'fecha' (fila 2): '2025-06-25'", ex.getMessage());
  }

  /**
   * Tests that {@code parseBigDecimal} returns zero when the input string is null or blank.
   */
  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return zero when parseBigDecimal input null or blank")
  void returnZeroWhenParseBigDecimalInputNullOrBlank(String input) {
    BigDecimal result = Functions.parseBigDecimal(input, 1, "campo");
    assertEquals(BigDecimal.ZERO, result);
  }

  /**
   * Tests that {@code parseBigDecimal} correctly parses various valid numeric formats.
   */
  @ParameterizedTest
  @ValueSource(strings = {"1234.56", "1,234.56", "- 123", "0", "-0"})
  @DisplayName("Return BigDecimal correct when parseBigDecimal with valid input")
  void returnBigDecimalCorrectWhenParseBigDecimalWithValidInput(String input) {
    BigDecimal result = Functions.parseBigDecimal(input, 1, "campo");
    BigDecimal expected = new BigDecimal(input.replace(",", "").replaceAll("-\\s+(\\d+)", "-$1"));
    assertEquals(0, expected.compareTo(result));
  }

  /**
   * Tests that {@code parseBigDecimal} throws {@link IllegalArgumentException} when the input
   * string is not a valid number.
   */
  @Test
  @DisplayName("Throw IllegalArgumentException when parseBigDecimal with invalid input")
  void throwIllegalArgumentExceptionWhenParseBigDecimalWithInvalidInput() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseBigDecimal("abc", 3, "monto"));
    assertEquals("Número inválido en 'monto' (fila 3): 'abc'", ex.getMessage());
  }

  /**
   * Tests that {@code parseDouble} returns zero when the input string is null or blank.
   */
  @ParameterizedTest
  @NullAndEmptySource
  @DisplayName("Return zero when parseDouble input null or blank")
  void returnZeroWhenParseDoubleInputNullOrBlank(String input) {
    Double result = Functions.parseDouble(input, 1, "campo");
    assertEquals(D_VAL_ZERO, result);
  }

  /**
   * Tests that {@code parseDouble} correctly parses various valid numeric formats.
   */
  @ParameterizedTest
  @ValueSource(strings = {"123.45", "1,234.56", "- 123.4", "0", "-0.0"})
  @DisplayName("Return double correct when parseDouble with valid input")
  void returnDoubleCorrectWhenParseDoubleWithValidInput(String input) {
    Double result = Functions.parseDouble(input, 1, "campo");
    Double expected = Double.parseDouble(input.replace(",", "").replaceAll("-\\s+(\\d+(\\.\\d+)?)", "-$1"));
    assertEquals(expected, result);
  }

  /**
   * Tests that {@code parseDouble} throws {@link IllegalArgumentException} when the input
   * string is not a valid double.
   */
  @Test
  @DisplayName("Throw IllegalArgumentException when parseDouble with invalid input")
  void throwIllegalArgumentExceptionWhenParseDoubleWithInvalidInput() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> Functions.parseDouble("xyz", 4, "monto"));
    assertEquals("Número double inválido en 'monto' (fila 4): 'xyz'", ex.getMessage());
  }

  /**
   * Tests that {@code defaultString} returns a blank string when the input is null.
   */
  @Test
  @DisplayName("Return blank when defaultString input null")
  void returnBlankWhenDefaultStringInputNull() {
    assertEquals(_BLANK, Functions.defaultString(null));
  }

  /**
   * Tests that {@code defaultString} returns the original string when the input is non-null.
   */
  @Test
  @DisplayName("Return original string when defaultString input non-null")
  void returnOriginalStringWhenDefaultStringInputNonNull() {
    String val = "texto";
    assertEquals(val, Functions.defaultString(val));
  }

  /**
   * Tests that {@link Functions#getStringOrDefault(ResultSet, String)} returns the actual
   * column value when the ResultSet value is non-null.
   */
  @Test
  @DisplayName("Return value when getStringOrDefault result not null")
  void returnValueWhenGetStringOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("col")).thenReturn("valor");
    assertEquals("valor", Functions.getStringOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getStringOrDefault(ResultSet, String)} returns an empty string
   * when the ResultSet value for the given column is null.
   */
  @Test
  @DisplayName("Return blank when getStringOrDefault result null")
  void returnBlankWhenGetStringOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getString("col")).thenReturn(null);
    assertEquals(_BLANK, Functions.getStringOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getBigDecimalOrDefault(ResultSet, String)} returns the actual
   * BigDecimal value from the ResultSet when it is non-null.
   */
  @Test
  @DisplayName("Return value when getBigDecimalOrDefault result not null")
  void returnValueWhenGetBigDecimalOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    BigDecimal val = new BigDecimal("123.45");
    when(rs.getBigDecimal("col")).thenReturn(val);
    assertEquals(val, Functions.getBigDecimalOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getBigDecimalOrDefault(ResultSet, String)} returns
   * {@link BigDecimal#ZERO} when the ResultSet value for the given column is null.
   */
  @Test
  @DisplayName("Return zero when getBigDecimalOrDefault result null")
  void returnZeroWhenGetBigDecimalOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getBigDecimal("col")).thenReturn(null);
    assertEquals(BigDecimal.ZERO, Functions.getBigDecimalOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getIntegerOrDefault(ResultSet, String)} returns the actual
   * Integer value when the ResultSet's getInt call yields a non-null result.
   */
  @Test
  @DisplayName("Return value when getIntegerOrDefault result not null")
  void returnValueWhenGetIntegerOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getInt("col")).thenReturn(5);
    assertEquals(Integer.valueOf(5), Functions.getIntegerOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getDateOrDefault(ResultSet, String)} converts a non-null
   * {@link java.sql.Date} to the correct {@link java.time.LocalDate}.
   */
  @Test
  @DisplayName("Return date when getDateOrDefault result not null")
  void returnDateWhenGetDateOrDefaultResultNotNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    java.sql.Date sqlDate = java.sql.Date.valueOf("2025-06-25");
    when(rs.getDate("col")).thenReturn(sqlDate);
    assertEquals(LocalDate.of(2025, 6, 25), Functions.getDateOrDefault(rs, "col"));
  }

  /**
   * Tests that {@link Functions#getDateOrDefault(ResultSet, String)} returns the default
   * {@link java.time.LocalDate} when the ResultSet's getDate call returns null.
   */
  @Test
  @DisplayName("Return default local date when getDateOrDefault result null")
  void returnDefaultLocalDateWhenGetDateOrDefaultResultNull() throws SQLException {
    ResultSet rs = mock(ResultSet.class);
    when(rs.getDate("col")).thenReturn(null);
    assertEquals(LOCAL_DATE_DEFAULT, Functions.getDateOrDefault(rs, "col"));
  }
}