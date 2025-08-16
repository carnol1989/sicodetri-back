package com.epsgrau.pe.sicodetri.helper;

import static com.epsgrau.pe.sicodetri.util.DataTest.createJasperPrintWithOneEmptyPage;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JasperReportsHelper}.
 *
 * <p>
 * Verifies that the {@code exportJasperReportBytes} method correctly exports
 * a {@link JasperPrint} to various report formats (CSV, XLSX, HTML, XML, DOCX, PDF)
 * and throws an exception when supplied a null {@code JasperPrint}.
 * </p>
 *
 * <b>Class</b>: {@link JasperReportsHelperTest}<br/>
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
@DisplayName("JasperReportsHelper Tests")
class JasperReportsHelperTest {

    /**
     * Creates a minimal {@link JasperPrint} containing one empty page
     * for use in export tests.
     *
     * @return a new empty {@link JasperPrint} with one blank page
     */
    private JasperPrint createEmptyJasperPrint() {
        return new JasperPrint();
    }

    /**
     * Tests exporting to CSV format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export CSV when JasperPrint is provided")
    void exportCsvWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createJasperPrintWithOneEmptyPage();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.CSV);

        // Assert
        assertNotNull(bytes, "CSV export should not return null");
        assertTrue(bytes.length > 0, "CSV export should produce some bytes");
    }

    /**
     * Tests exporting to XLSX format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export XLSX when JasperPrint is provided")
    void exportXlsxWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createEmptyJasperPrint();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.XLSX);

        // Assert
        assertNotNull(bytes, "XLSX export should not return null");
        assertTrue(bytes.length > 0, "XLSX export should produce some bytes");
    }

    /**
     * Tests exporting to HTML format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export HTML when JasperPrint is provided")
    void exportHtmlWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createEmptyJasperPrint();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.HTML);

        // Assert
        assertNotNull(bytes, "HTML export should not return null");
        assertTrue(bytes.length > 0, "HTML export should produce some bytes");
    }

    /**
     * Tests exporting to XML format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export XML when JasperPrint is provided")
    void exportXmlWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createEmptyJasperPrint();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.XML);

        // Assert
        assertNotNull(bytes, "XML export should not return null");
        assertTrue(bytes.length > 0, "XML export should produce some bytes");
    }

    /**
     * Tests exporting to DOCX (RTF) format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export DOCX (RTF) when JasperPrint is provided")
    void exportDocxWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createEmptyJasperPrint();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.DOCX);

        // Assert
        assertNotNull(bytes, "DOCX (RTF) export should not return null");
        assertTrue(bytes.length > 0, "DOCX (RTF) export should produce some bytes");
    }

    /**
     * Tests exporting to PDF format produces a non-null, non-empty byte array.
     *
     * @throws JRException if export fails
     */
    @Test
    @DisplayName("Export PDF (default) when JasperPrint is provided")
    void exportPdfWhenJasperPrintIsProvided() throws JRException {
        // Arrange
        JasperPrint jasperPrint = createEmptyJasperPrint();

        // Act
        byte[] bytes = JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.PDF);

        // Assert
        assertNotNull(bytes, "PDF export should not return null");
        assertTrue(bytes.length > 0, "PDF export should produce some bytes");
    }

    /**
     * Tests that passing a null {@link JasperPrint} causes a {@link JRException}
     * with an appropriate error message.
     */
    @Test
    @DisplayName("Throw JRException when JasperPrint is null")
    void throwJrexceptionWhenJasperPrintIsNull() {
        // Arrange
        JasperPrint jasperPrint = null;

        // Act & Assert
        JRException exception = assertThrows(
                JRException.class,
                () -> JasperReportsHelper.exportJasperReportBytes(jasperPrint, ReportTypeEnum.PDF),
                "Exporting with null JasperPrint should throw JRException"
        );
        assertTrue(exception.getMessage().contains("JasperPrint no puede ser null"),
                "Exception message should indicate JasperReports issue");
    }
}