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
 * Unit tests for {@link ExcelHelper}.
 *
 * <p>
 * Verifies:
 * <ul>
 *   <li>Detection of valid XLSX content types</li>
 *   <li>Rejection of non-XLSX content types</li>
 *   <li>Extraction of string, numeric, date, formula, and boolean values from cells</li>
 * </ul>
 * </p>
 *
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
 *     <li>Jun 06, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
class ExcelHelperTest {

    @Mock
    private MultipartFile file;

    /**
     * Verifies that {@link ExcelHelper#hasExcelFormat(MultipartFile)} returns true
     * when the file content type is XLSX.
     *
     * @throws IOException if creating the mock input stream fails
     */
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

    /**
     * Verifies that {@link ExcelHelper#hasExcelFormat(MultipartFile)} returns false
     * when the file content type is not XLSX.
     *
     * @throws IOException if mock creation fails
     */
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

    /**
     * Verifies that {@link ExcelHelper#getValueCell(Cell)} returns the string cell value
     * when the cell type is STRING.
     */
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

    /**
     * Verifies that {@link ExcelHelper#getValueCell(Cell)} returns the numeric value
     * as a string when the cell type is NUMERIC and not date formatted.
     */
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

    /**
     * Verifies that {@link ExcelHelper#getValueCell(Cell)} returns a formatted date string
     * when the cell type is NUMERIC and the cell is date formatted.
     *
     * @throws ParseException if the test date string cannot be parsed
     */
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

    /**
     * Verifies that {@link ExcelHelper#getValueCell(Cell)} returns the cell formula
     * string when the cell type is FORMULA.
     */
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

    /**
     * Verifies that {@link ExcelHelper#getValueCell(Cell)} returns the boolean value
     * when the cell type is BOOLEAN.
     */
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