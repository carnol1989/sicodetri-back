package com.epsgrau.pe.sicodetri.helper;

import static com.epsgrau.pe.sicodetri.helper.FileExcelHelperTest.createMockExcelInputStreamData;
import static com.epsgrau.pe.sicodetri.helper.FilePdfHelperTest.createMockPdfInputStreamData;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epsgrau.pe.sicodetri.util.ConstantsTest;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @DisplayName("Test hasExcelFormat with valid Excel format")
    void testHasExcelFormatWithValidExcelFormat() throws IOException {
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
    @DisplayName("Test hasExcelFormat with invalid Excel format")
    void testHasExcelFormatWithInvalidExcelFormat() throws IOException {
        // Arrange
        ByteArrayInputStream bais = createMockPdfInputStreamData();
        MultipartFile file = mock(MultipartFile.class);

        when(file.getContentType()).thenReturn("application/pdf");

        // Act
        boolean result = ExcelHelper.hasExcelFormat(file);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Test getValueCell with STRING type")
    void testGetValueCellWithStringType() {
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
    @DisplayName("Test getValueCell with NUMERIC type")
    void testGetValueCellWithNumericType() {
        // Arrange
        Cell cell = mock(Cell.class);
        when(cell.getCellType()).thenReturn(CellType.NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(12345.67);

        // Act
        Object result = ExcelHelper.getValueCell(cell);

        // Assert
        assertEquals(12345.67, result);
    }

    @Test
    @DisplayName("Test getValueCell with DATE type")
    void testGetValueCellWithDateType() throws ParseException {
        // Arrange
        Cell cell = mock(Cell.class);
        SimpleDateFormat formatter = new SimpleDateFormat(ConstantsTest.FORMATTER_YYYY_MM_DD);
        String dateString = "2024-08-08";
        Date parsedDate = formatter.parse(dateString);

        try (MockedStatic<DateUtil> mockedDateUtil = Mockito.mockStatic(DateUtil.class)) {
            // Mockear el método estático
            mockedDateUtil.when(() -> DateUtil.isCellDateFormatted(cell)).thenReturn(true);

            // Configurar el comportamiento de los métodos no estáticos
            when(cell.getCellType()).thenReturn(CellType.NUMERIC);
            when(cell.getDateCellValue()).thenReturn(parsedDate);

            // Act
            Object result = ExcelHelper.getValueCell(cell);

            // Assert
            assertNotNull(result);
            // Opcional: verificar el formato de la fecha devuelta
            String expectedDate = formatter.format(parsedDate);
            assertEquals(expectedDate, result);
        }
    }

    @Test
    @DisplayName("Test getValueCell with FORMULA type")
    void testGetValueCellWithFormulaType() {
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
    @DisplayName("Test getValueCell with BOOLEAN type")
    void testGetValueCellWithBooleanType() {
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