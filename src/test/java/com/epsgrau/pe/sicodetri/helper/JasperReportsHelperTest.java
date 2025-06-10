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

@DisplayName("JasperReportsHelper Tests")
class JasperReportsHelperTest {

    /**
     * Helper to create a minimal JasperPrint for export tests.
     * An empty JasperPrint (no pages) is sufficient to verify exporter behavior.
     */
    private JasperPrint createEmptyJasperPrint() {
        return new JasperPrint();
    }

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

    @Test
    @DisplayName("Throw JRException when JasperPrint is null")
    void throwJrExceptionWhenJasperPrintIsNull() {
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