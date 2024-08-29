package com.epsgrau.pe.sicodetri.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class is a util for constants. <br/>
 * <b>Class</b>: {@link Constants}<br/>
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
public class Constants {

    public static String LIST_INPUT_DATA = "listInputData";
    public static String TYPE_XLS = "application/vnd.ms-excel";
    public static String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static BigDecimal BD_VAL_100 = new BigDecimal("100");
    public static BigDecimal BD_VAL_12 = new BigDecimal("12");

    public static int N_VAL_0 = 0;
    public static int N_VAL_1 = 1;
    public static int N_VAL_2 = 2;
    public static int N_VAL_3 = 3;
    public static String _BLANK = "";
    public static String S_VAL_ZERO = "0";
    public static String S_VAL_332 = "332";
    public static Double D_VAL_ZERO = 0.0;

    public static String S_DATE_DEFAULT = "1900-01-01";
    public static String FORMATTER_DD_MM_YYYY = "dd/MM/yyyy";
    public static String FORMATTER_YYYY_MM_DD = "yyyy-MM-dd";
    public static LocalDate LOCAL_DATE_DEFAULT = LocalDate.parse("1900-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    public static String[] HEADERS = {"CÓDIGO ACTIVO FIJO PRINCIPAL",	"CÓDIGO ACTIVO FIJO COMPONENTE",	"CUENTA CONTABLE DEL ACTIVO FIJO",
            "DESCRIPCIÓN",	"MARCA DEL ACTIVO FIJO",	"MODELO DEL ACTIVO FIJO",	"NÚMERO DE SERIE Y/O PLACA DEL ACTIVO FIJO",
            "SALDO INICIAL AL 31.12.2020",	"ADQUISICIONES ADICIONES",	"MEJORAS",	"TRANSFERENCIAS Y/O RECLASIFICACIONES",
            "RETIROS Y/O BAJAS",	"OTROS AJUSTES",	"VALOR EN LIBROS DEL ACTIVO FIJO AL 31.12.2022",	"AJUSTE POR INFLACIÓN",
            "VALOR AJUSTADO DEL ACTIVO FIJO AL 31.12.2022",	"FECHA DE ADQUISICIÓN",	"FECHA DE INICIO DEL USO DEL ACTIVO FIJO",
            "FECHA DE RETIRO", "MÉTODO APLICADO",	"N° DE DOCUMENTO DE AUTORIZACIÓN",	"AÑOS VIDA UTIL", "DEPRECIACIÓN DE ACUMULADA AL AL CIERRE DEL EJERCICIO ANTERIOR",
            "DEPRECIACIÓN DEL EJERCICIO", "DEPRECIACIÓN RELACIONADA CON LOS RETIROS Y/O BAJAS", "DEPRECIACIÓN DEL EJERCICIO RELACIONADA CON OTROS AJUSTES",
            "DEPRECIACIÓN ACUMULADA HISTÓRICA", "AJUSTE POR INFLACIÓN DE LA DEPRECIACIÓN",	"DEPRECIACIÓN ACUMULADA AJUSTADA POR INFLACIÓN",
            "", "ACTIVO FIJO NETO"};

}
