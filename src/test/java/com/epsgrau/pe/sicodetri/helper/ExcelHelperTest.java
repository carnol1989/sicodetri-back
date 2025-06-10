package com.epsgrau.pe.sicodetri.helper;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelInputStreamData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epsgrau.pe.sicodetri.util.ConstantsTest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class test for ExcelHelper. <br/>
 * <b>Class</b>: {@link ExcelHelperTest}<br/>
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
class ExcelHelperTest {

    @Mock
    private MultipartFile file;

    @Test
    @DisplayName("Return true when file content type is XLSX")
    void returnTrueWhenFileContentTypeIsXlsx() throws IOException {
        // Arrange
        ByteArrayInputStream bais = createMockExcelInputStreamData();
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn(ConstantsTest.TYPE_XLSX);

        // Act
        boolean result = ExcelHelper.hasExcelFormat(file);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Return false when file content type is not XLSX")
    void returnFalseWhenFileContentTypeIsNotXlsx() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getContentType()).thenReturn("application/pdf");

        // Act
        boolean result = ExcelHelper.hasExcelFormat(file);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Return string value when cell type is STRING")
    void returnStringValueWhenCellTypeIsString() {
        // Arrange
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(CellType.STRING);
        when(cell.getStringCellValue()).thenReturn("Test String");

        // Act
        Object result = ExcelHelper.getValueCell(cell);

        // Assert
        assertEquals("Test String", result);
    }

    @Test
    @DisplayName("Return numeric value when cell type is NUMERIC and not date formatted")
    void returnNumericValueWhenCellTypeIsNumericAndNotDate() {
        // Arrange
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(12345.67);

        // Act
        Object result = ExcelHelper.getValueCell(cell);

        // Assert
        assertEquals(String.valueOf(12345.67), result);
    }

    @Test
    @DisplayName("Return formatted date string when cell is date formatted")
    void returnFormattedDateStringWhenCellIsDateFormatted() throws ParseException {
        // Arrange
        Cell cell = mock(Cell.class);
        SimpleDateFormat formatter = new SimpleDateFormat(ConstantsTest.FORMATTER_YYYY_MM_DD);
        String dateString = "2024-08-08";
        Date parsedDate = formatter.parse(dateString);

        try (MockedStatic<DateUtil> mockedDateUtil = Mockito.mockStatic(DateUtil.class)) {
            mockedDateUtil.when(() -> DateUtil.isCellDateFormatted(cell)).thenReturn(true);

            when(cell.getCellType()).thenReturn(CellType.NUMERIC);
            when(cell.getDateCellValue()).thenReturn(parsedDate);

            // Act
            Object result = ExcelHelper.getValueCell(cell);

            // Assert
            assertNotNull(result);
            String expectedDate = formatter.format(parsedDate);
            assertEquals(expectedDate, result);
        }
    }

    @Test
    @DisplayName("Return formula string when cell type is FORMULA")
    void returnFormulaStringWhenCellTypeIsFormula() {
        // Arrange
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(CellType.FORMULA);
        when(cell.getCellFormula()).thenReturn("A1+B1");

        // Act
        Object result = ExcelHelper.getValueCell(cell);

        // Assert
        assertEquals("A1+B1", result);
    }

    @Test
    @DisplayName("Return boolean value when cell type is BOOLEAN")
    void returnBooleanValueWhenCellTypeIsBoolean() {
        // Arrange
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(CellType.BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(true);

        // Act
        Object result = ExcelHelper.getValueCell(cell);

        // Assert
        assertEquals(true, result);
    }

}