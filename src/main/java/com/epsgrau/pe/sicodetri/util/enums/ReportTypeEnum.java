package com.epsgrau.pe.sicodetri.util.enums;

/**
 * This enum for report type permitted. <br/>
 * <b>Class</b>: {@link ReportTypeEnum}<br/>
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
 *     <li>Sep 08, 2024 (JAR) Creation class.</li>
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

    ReportTypeEnum(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Get report type enum permitted.
     *
     * @param code Code report type
     * @return ReportTypeEnum
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
