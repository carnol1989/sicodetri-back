package com.epsgrau.pe.sicodetri.util;

import java.math.BigDecimal;

/**
 * Collection of constants used in unit tests across the Sicodetri project.
 *
 * <p>
 * Provides string keys, MIME types, numeric values, format patterns, and stored
 * procedure definitions to simplify test setup and verification.
 * </p>
 *
 * <b>Class</b>: {@link ConstantsTest}<br/>
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class ConstantsTest {

    public static String LIST_INPUT_DATA = "listInputData";
    public static String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final BigDecimal BD_VAL_100 = new BigDecimal("100");

    public static int N_VAL_1 = 1;
    public static int N_VAL_2 = 2;
    public static int N_VAL_3 = 3;
    public static final int N_VAL_9 = 9;

    public static Long L_VAL_1 = 1L;
    public static Long L_VAL_2 = 2L;
    public static Long L_VAL_3 = 3L;

    public static String S_VAL_00 = "00";
    public static String S_VAL_01 = "01";
    public static final String S_VAL_02 = "02";
    public static final String S_VAL_03 = "03";
    public static String S_VAL_332 = "332";

    public static Double D_VAL_ZERO = 0.0;

    public static String FORMATTER_DD_MM_YYYY = "dd/MM/yyyy";
    public static String FORMATTER_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String SP_TAX_BOOK =
        "CALL get_report_tax_book(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final int P_YEAR      = 1;
    public static final int P_MAIN_CODE = 2;
    public static final int P_ACCOUNT   = 3;
    public static final int P_START_ACQ = 4;
    public static final int P_END_ACQ   = 5;
    public static final int P_START_USE = 6;
    public static final int P_END_USE   = 7;
    public static final int P_OUT_CODE  = 8;
    public static final int P_OUT_MSG   = 9;
    public static final int P_OUT_CUR   = 10;

}