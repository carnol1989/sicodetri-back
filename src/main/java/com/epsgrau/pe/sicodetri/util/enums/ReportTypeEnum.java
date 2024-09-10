package com.epsgrau.pe.sicodetri.util.enums;

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

    public static ReportTypeEnum getReportTypeByCode(String code) {
        for (ReportTypeEnum type : values()) {
            if (type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de reporte desconocido: " + code);
    }

}
