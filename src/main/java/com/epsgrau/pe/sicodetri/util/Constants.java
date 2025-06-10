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

    public static final String CALL_SQL =
        "CALL get_report_tax_book(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final int IDX_YEAR = 1;
    public static final int IDX_MAIN_CODE = 2;
    public static final int IDX_ACCOUNTING_ACCOUNT = 3;
    public static final int IDX_START_ACQ_DATE = 4;
    public static final int IDX_END_ACQ_DATE = 5;
    public static final int IDX_START_DATE_USE = 6;
    public static final int IDX_END_DATE_USE = 7;
    public static final int IDX_OUT_ERROR_CODE = 8;
    public static final int IDX_OUT_ERROR_MESSAGE = 9;
    public static final int IDX_OUT_RESULT_SET_CURSOR = 10;

    public static final String LIST_INPUT_DATA = "listInputData";
    public static final String TYPE_XLSX = "application/vnd.openxmlformats-officedocument"
        + ".spreadsheetml.sheet";

    public static final String COMPANY_NAME = "companyName";
    public static final String COMPANY_NAME_VALUE = "EPS GRAU S.A";
    public static final String PERIOD = "period";
    public static final String RUC = "ruc";
    public static final String RUC_VALUE = "2010276292";
    public static final String ALL = "ALL";

    public static final BigDecimal BD_VAL_100 = new BigDecimal("100");
    public static final BigDecimal BD_VAL_12 = new BigDecimal("12");

    public static final int N_VAL_0 = 0;
    public static final int N_VAL_1 = 1;
    public static final int N_VAL_2 = 2;
    public static final int N_VAL_3 = 3;
    public static final int N_VAL_9 = 9;
    public static final int N_VAL_15 = 15;
    public static final String _BLANK = "";
    public static final String S_VAL_00 = "00";
    public static final String S_VAL_01 = "01";
    public static final String S_VAL_02 = "02";
    public static final String S_VAL_332 = "332";
    public static final String S_VAL_333 = "333";
    public static final String S_VAL_334 = "334";
    public static final String S_VAL_335 = "335";
    public static final String S_VAL_336 = "336";
    public static final String S_VAL_337 = "337";

    public static final Double D_VAL_ZERO = 0.0;

    public static final String S_DATE_DEFAULT = "1900-01-01";
    public static final String FORMATTER_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String FORMATTER_YYYY_MM_DD = "yyyy-MM-dd";
    public static final LocalDate LOCAL_DATE_DEFAULT = LocalDate.parse("1900-01-01",
        DateTimeFormatter.ofPattern("yyyy-MM-dd"));

}
