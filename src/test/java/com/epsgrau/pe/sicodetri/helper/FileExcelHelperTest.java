package com.epsgrau.pe.sicodetri.helper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class to handle Excel data and methods. <br/>
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
 *     <li>Ago 18, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class FileExcelHelperTest {

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

    public static void createRowHeaderFile(Sheet sheet, int repetitions) {
        for (int i = 0; i < repetitions; i++) {
            Row row = sheet.createRow(i);
        }
    }

}
