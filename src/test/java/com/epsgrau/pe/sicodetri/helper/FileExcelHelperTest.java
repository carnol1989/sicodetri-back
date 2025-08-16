package com.epsgrau.pe.sicodetri.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility methods for creating mock Excel workbooks as input streams
 * for use in unit and integration tests.
 *
 * <p>
 * Provides methods to generate various predefined Excel structures:
 * <ul>
 *   <li>Sheets with mixed data types: strings, numbers, dates, formulas, booleans</li>
 *   <li>Domain-specific report format with header rows and asset detail rows</li>
 *   <li>Empty workbooks or malformed streams to simulate failure</li>
 * </ul>
 * </p>
 *
 * <b>Class</b>: {@link FileExcelHelperTest}<br/>
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
public class FileExcelHelperTest {

    /**
     * Creates an in-memory XLSX workbook with one sheet containing a single row
     * populated with a string, numeric, date, formula, and boolean cell.
     *
     * @return a {@link ByteArrayInputStream} of the workbook bytes
     * @throws IOException if an I/O error occurs during workbook creation
     */
    public static ByteArrayInputStream createMockExcelInputStreamData() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("Hoja1");

        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Test String");
        row.createCell(1).setCellValue(12345.67);
        row.createCell(2).setCellValue(new java.util.Date());
        row.createCell(3).setCellValue("A1+B1");
        row.createCell(4).setCellValue(true);

        workbook.write(out);
        workbook.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());

        out.close();

        return bais;
    }

    /**
     * Creates an in-memory XLSX workbook formatted like the "Libro Tributario" report.
     * <p>
     * Includes header rows, blank rows, metadata (period, RUC, entity name),
     * column titles, and one data row with multiple columns for asset details.
     * </p>
     *
     * @return a {@link ByteArrayInputStream} of the workbook bytes
     * @throws IOException if an I/O error occurs during workbook creation
     */
    public static ByteArrayInputStream createMockExcelInputStream() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Sheet sheet = workbook.createSheet("CTA33");

        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("FORMATO 7.1: REGISTRO DE ACTIVOS FIJOS - DETALLE DE LOS ACTIVOS FIJOS - FINANCIERO");

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("");

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("PERÍODO:");
        row2.createCell(1).setCellValue("");
        row2.createCell(2).setCellValue("");
        row2.createCell(3).setCellValue("31/12/2022");

        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("RUC:");
        row3.createCell(1).setCellValue("");
        row3.createCell(2).setCellValue("");
        row3.createCell(3).setCellValue("2010276292");

        Row row4 = sheet.createRow(4);
        row4.createCell(0).setCellValue("ENTIDAD PRESTADORA DE SERVICIOS DE SANEAMIENTO GRAU S.A");

        Row row5 = sheet.createRow(5);
        row5.createCell(0).setCellValue("");

        Row row6 = sheet.createRow(6);
        row6.createCell(0).setCellValue("");

        Row row7 = sheet.createRow(7);
        row7.createCell(0).setCellValue("CÓDIGO");

        Row row8 = sheet.createRow(8);
        row8.createCell(0).setCellValue("ACTIVO");

        Row row9 = sheet.createRow(9);
        row9.createCell(0).setCellValue("FIJO");

        Row row10 = sheet.createRow(10);
        row10.createCell(0).setCellValue("PRINCIPAL");

        Row row = sheet.createRow(11);
        row.createCell(0).setCellValue("21.02.010149");
        row.createCell(1).setCellValue("");
        row.createCell(2).setCellValue("01261110039");
        row.createCell(3).setCellValue("TERRENO LAGUNA OXIDACION PAITA");
        row.createCell(4).setCellValue("NINGUNA");
        row.createCell(5).setCellValue("NINGUNO");
        row.createCell(6).setCellValue("/");
        row.createCell(7).setCellValue(0);
        row.createCell(8).setCellValue(0);
        row.createCell(9).setCellValue(0);
        row.createCell(10).setCellValue(0);
        row.createCell(11).setCellValue(0);
        row.createCell(12).setCellValue(0);
        row.createCell(13).setCellValue(0);
        row.createCell(14).setCellValue(0);
        row.createCell(15).setCellValue(0);
        row.createCell(16).setCellValue("26/09/2012");
        row.createCell(17).setCellValue("26/09/2012");
        row.createCell(18).setCellValue("");
        row.createCell(19).setCellValue("LINEA RECTA");
        row.createCell(20).setCellValue("310-1997-1998");
        row.createCell(21).setCellValue(0);
        row.createCell(22).setCellValue(0);
        row.createCell(23).setCellValue(0);
        row.createCell(24).setCellValue(0);
        row.createCell(25).setCellValue(0);
        row.createCell(26).setCellValue(0);
        row.createCell(27).setCellValue(0);
        row.createCell(28).setCellValue(0);
        row.createCell(30).setCellValue(0);

        workbook.write(out);
        workbook.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());

        out.close();

        return bais;
    }

    /**
     * Creates an empty XLSX workbook with a single empty sheet named "CTA33".
     *
     * @return a {@link ByteArrayInputStream} of the empty workbook bytes
     * @throws IOException if an I/O error occurs during workbook creation
     */
    public static ByteArrayInputStream createMockExcelEmpty() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.createSheet("CTA33");
        workbook.write(out);
        workbook.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
        out.close();
        return bais;
    }

    /**
     * Creates a malformed Excel input stream by writing an empty workbook
     * without any sheets or rows configured.
     *
     * @return a {@link ByteArrayInputStream} representing an invalid Excel stream
     * @throws IOException if an I/O error occurs during workbook creation
     */
    public static ByteArrayInputStream createMockExcelInputStreamFailed() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Sheet sheet = null;
        Row row = null;

        workbook.write(out);
        workbook.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());

        out.close();

        return bais;
    }

    /**
     * Adds the specified number of empty rows at the top of the given sheet
     * to simulate a header region.
     *
     * @param sheet       the {@link Sheet} in which to create rows
     * @param repetitions the number of empty rows to create
     */
    public static void createRowHeaderFile(Sheet sheet, int repetitions) {
        for (int i = 0; i < repetitions; i++) {
            Row row = sheet.createRow(i);
        }
    }

}
