package com.epsgrau.pe.sicodetri.util;

import static com.epsgrau.pe.sicodetri.util.Constants.D_VAL_ZERO;
import static com.epsgrau.pe.sicodetri.util.Constants.LOCAL_DATE_DEFAULT;
import static com.epsgrau.pe.sicodetri.util.Constants.N_VAL_0;
import static com.epsgrau.pe.sicodetri.util.Constants._BLANK;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * This class is a util for functions. <br/>
 * <b>Class</b>: {@link Functions}<br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class Functions {

    /**
     * Convert Date to LocalDate string.
     *
     * @param dateIn Date input
     * @param format Format date
     * @return String LocalDate
     */
    public static String convertDateToLocalDateString(Date dateIn, String format) {
        // Convertir Date a Instant
        Instant instant = dateIn.toInstant();

        // Convertir Instant a LocalDate utilizando la zona horaria del sistema
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        // Definir el formato deseado para la salida
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        // Formatear el LocalDate a una cadena con el formato deseado
        String dateFormat = localDate.format(formatter);

        String[] dateSplit = dateFormat.split("/");
        String dateNew = dateSplit[2] + "-" + dateSplit[1] + "-" + dateSplit[0];

        return dateNew;
    }

    /**
     * Validate and analyze dates.
     *
     * @param dateStr Date string
     * @param formatter Formatter
     * @param rowNum Row num
     * @param fieldName Field name
     * @return LocalDate
     */
    public static LocalDate parseDate(String dateStr, DateTimeFormatter formatter,
                                       int rowNum, String fieldName) {
        if (dateStr == null || dateStr.isBlank()) {
            return LOCAL_DATE_DEFAULT;
        }
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido en '" + fieldName
                + "' (fila " + rowNum + "): '" + dateStr + "'");
        }
    }

    /**
     * Numerical (BigDecimal) parsing with validation.
     *
     * @param value String for parse
     * @param rowNum Row num
     * @param fieldName Field name
     * @return BigDecimal
     */
    public static BigDecimal parseBigDecimal(String value, int rowNum, String fieldName) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            // 1. Elimina separadores de miles (comas)
            String normalizedValue = value.replace(",", "");

            // 2. Reemplaza "- 0" o cualquier "- <espacio><dígitos>" por "-<dígitos>"
            normalizedValue = normalizedValue.replaceAll("-\\s+(\\d+)", "-$1");

            return new BigDecimal(normalizedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número inválido en '" + fieldName + "' (fila "
                + rowNum + "): '" + value + "'");
        }
    }

    /**
     * Numerical (Double) parsing with validation.
     *
     * @param value String
     * @param rowNum Row num
     * @param fieldName Field name
     * @return Double
     */
    public static Double parseDouble(String value, int rowNum, String fieldName) {
        if (value == null || value.isBlank()) {
            return D_VAL_ZERO;
        }
        try {
            String normalizedValue = value.replace(",", "");

            normalizedValue = normalizedValue.replaceAll("-\\s+(\\d+(\\.\\d+)?)", "-$1");

            return Double.parseDouble(normalizedValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número double inválido en '" + fieldName
                + "' (fila " + rowNum + "): '" + value + "'");
        }
    }

    /**
     * Validate string and return default value.
     *
     * @param value String
     * @return String
     */
    public static String defaultString(String value) {
        return (value != null) ? value : _BLANK;
    }

    /**
     * If the column string is null, returns BLANK; otherwise, the value.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return String
     * @throws SQLException SQL Exception
     */
    public static String getStringOrDefault(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        return (val != null) ? val : _BLANK;
    }

    /**
     * If the column's BigDecimal is null, returns BigDecimal.ZERO; otherwise, the value.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return BigDecimal
     * @throws SQLException SQL Exception
     */
    public static BigDecimal getBigDecimalOrDefault(ResultSet rs, String column)
        throws SQLException {
        BigDecimal val = rs.getBigDecimal(column);
        return (val != null) ? val : BigDecimal.ZERO;
    }

    public static Integer getIntegerOrDefault(ResultSet rs, String column) throws SQLException {
        Integer val = rs.getInt(column);
        return (val != null) ? val : N_VAL_0;
    }

    /**
     * If the date column is null, returns LOCAL_DATE_DEFAULT; otherwise, converts it to LocalDate.
     *
     * @param rs ResultSet
     * @param column Column name
     * @return LocalDate
     * @throws SQLException SQL Exception
     */
    public static LocalDate getDateOrDefault(ResultSet rs, String column) throws SQLException {
        java.sql.Date d = rs.getDate(column);
        return (d != null) ? d.toLocalDate() : LOCAL_DATE_DEFAULT;
    }

}
