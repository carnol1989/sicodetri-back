package com.epsgrau.pe.sicodetri.helper;

import com.epsgrau.pe.sicodetri.util.enums.ReportTypeEnum;
import java.io.ByteArrayOutputStream;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

/**
 * Utility class for exporting JasperPrint objects to various report formats.
 * Supports CSV, XLSX, HTML, XML, DOCX (RTF), and PDF outputs.
 * <b>Class</b>: {@link JasperReportsHelper}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
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
public class JasperReportsHelper {

    /**
     * Exports the given {@link JasperPrint} to a byte array in the specified format.
     * If the {@code jasperPrint} parameter is null, a {@link JRException} is thrown.
     * Otherwise, the report is exported according to {@code reportType}:
     * <ul>
     *   <li>CSV → {@link JRCsvExporter}</li>
     *   <li>XLSX → {@link JRXlsxExporter}</li>
     *   <li>HTML → {@link HtmlExporter}</li>
     *   <li>XML → {@link JRXmlExporter}</li>
     *   <li>DOCX (RTF) → {@link JRRtfExporter}</li>
     *   <li>PDF (default) → {@link JasperExportManager}</li>
     * </ul>
     *
     * @param jasperPrint the JasperPrint object to export; must not be null
     * @param reportType  the target report format (CSV, XLSX, HTML, XML, DOCX, PDF)
     * @return a byte array containing the exported report data
     * @throws JRException if {@code jasperPrint} is null or an error occurs during export
     */
    public static byte[] exportJasperReportBytes(JasperPrint jasperPrint,
                                                 ReportTypeEnum reportType) throws JRException {
        if (jasperPrint == null) {
            throw new JRException("JasperPrint no puede ser null al exportar reporte.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        switch (reportType) {
          case CSV:
              // Export to CSV
              JRCsvExporter csvExporter = new JRCsvExporter();
              csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
              csvExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
              csvExporter.exportReport();
              break;
          case XLSX:
              // Export to XLSX
              JRXlsxExporter xlsxExporter = new JRXlsxExporter();
              xlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
              xlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
              xlsxExporter.exportReport();
              break;
          case HTML:
              // Export to HTML
              HtmlExporter htmlExporter = new HtmlExporter();
              htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
              htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
              htmlExporter.exportReport();
              break;
          case XML:
              // Export to XML
              JRXmlExporter xmlExporter = new JRXmlExporter();
              xmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
              xmlExporter.setExporterOutput(new SimpleXmlExporterOutput(outputStream));
              xmlExporter.exportReport();
              break;
          case DOCX:
              // Export to DOCX (RTF format)
              JRRtfExporter docxExporter = new JRRtfExporter();
              docxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
              docxExporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
              docxExporter.exportReport();
              break;
          default:
              // Export to PDF by default
              JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
              break;
        }
        return outputStream.toByteArray();
    }
}
