package com.epsgrau.pe.sicodetri.util.enums;

/**
 * Enum representing the permitted report output formats and their corresponding file extensions.
 * Each constant defines a report type (e.g., PDF, CSV, XLSX) and the extension to use
 * when generating or exporting the report file.
 * <b>Class</b>: {@link ReportTypeEnum}<br/>
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
public enum ReportTypeEnum {

    PDF(".pdf"),
    CSV(".csv"),
    HTML(".html"),
    XML(".xml"),
    XLS(".xls"),
    XLSX(".xlsx"),
    DOCX(".docx");

    private String extension;

    /**
     * Constructor.
     *
     * @param extension the file extension associated with this report type (including
     *                  the leading dot)
     */
    ReportTypeEnum(String extension) {
        this.extension = extension;
    }

    /**
     * Returns the file extension for this report type.
     *
     * @return the extension, e.g. ".pdf"
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Looks up a {@code ReportTypeEnum} by its name (code) in a case-insensitive manner.
     *
     * @param code the name of the report type (e.g. "PDF", "xlsx")
     * @return the corresponding {@code ReportTypeEnum}
     * @throws IllegalArgumentException if no matching type exists
     */
    public static ReportTypeEnum getReportTypeByCode(String code) {
        for (ReportTypeEnum type : values()) {
            if (type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de reporte desconocido: " + code);
    }

}
