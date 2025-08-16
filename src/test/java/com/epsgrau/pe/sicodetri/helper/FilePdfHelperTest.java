package com.epsgrau.pe.sicodetri.helper;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility methods for creating mock PDF documents as input streams
 * for use in unit and integration tests.
 *
 * <p>
 * Provides a method to generate a minimal PDF containing a simple paragraph.
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
public class FilePdfHelperTest {

    /**
     * Creates an in-memory PDF document with a single paragraph of text.
     * <p>
     * The returned {@link ByteArrayInputStream} can be used to simulate
     * file uploads or PDF parsing operations in tests.
     * </p>
     *
     * @return a {@link ByteArrayInputStream} containing the PDF bytes
     * @throws IOException if an I/O error occurs during PDF generation
     */
    public static ByteArrayInputStream createMockPdfInputStreamData() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello, iText PDF with Spring Boot!"));

        document.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());

        return bais;
    }

}
