package com.epsgrau.pe.sicodetri.helper;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
