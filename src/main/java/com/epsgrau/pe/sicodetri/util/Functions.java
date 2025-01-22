package com.epsgrau.pe.sicodetri.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

}
