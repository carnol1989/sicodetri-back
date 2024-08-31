package com.epsgrau.pe.sicodetri.helper;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

        createRowHeaderFile(sheet, 12);
        Row row = sheet.createRow(12);
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
