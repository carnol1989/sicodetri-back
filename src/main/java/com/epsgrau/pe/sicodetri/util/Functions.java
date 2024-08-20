package com.epsgrau.pe.sicodetri.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Functions {

    public static String convertDateToLocalDateString(Date dateIn, String format) {
        // Convertir Date a Instant
        Instant instant = dateIn.toInstant();

        // Convertir Instant a LocalDate utilizando la zona horaria del sistema
        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        // Definir el formato deseado para la salida
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        // Formatear el LocalDate a una cadena con el formato deseado
        return localDate.format(formatter);
    }

}
