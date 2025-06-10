package com.epsgrau.pe.sicodetri.helper;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Utility class to handle PDF data and methods. <br/>
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
 *     <li>Ago 28, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public class FilePdfHelperTest {

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
