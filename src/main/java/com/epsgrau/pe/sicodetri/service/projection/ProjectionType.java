package com.epsgrau.pe.sicodetri.service.projection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the different types of fixed asset projection generation.
 * Each value has a code (for HTTP/JSON exchange) and a human-readable description. <br/>
 * <b>Class</b>: {@link ProjectionType}<br/>
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
 *     <li>Jun 11, 2025 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
public enum  ProjectionType {

    /**
     * Projection of temporary deductions.
     */
    TEMPORARY_DEDUCTIONS("TEMPORARY_DEDUCTION", "Deducción temporal"),

    /**
     * Projection of temporary additions.
     */
    TEMPORARY_ADDITIONS("TEMPORARY_ADDITION", "Adición temporal");

    /**
     * Code to be used in the query-param or in JSON (e.g.: ?type=TEMPORARY_DEDUCTION).
     */
    private final String code;

    /**
     * Friendly description for logs or documentation.
     */
    private final String description;

    /**
     * Constructor.
     *
     * @param code Code
     * @param description Description
     */
    ProjectionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the code for this projection type. Annotated with @JsonValue so
     * Jackson can automatically serialize it.
     *
     * @return Code
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Useful short description to display in interfaces or logs.
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Creates an instance of ProjectionType from code.
     * Case-insensitive comparison.
     *
     * @param value The code received (e.g. "TEMPORARY_ADDITION")
     * @return The corresponding value of the enum
     * @throws IllegalArgumentException If a valid code does not exist
     */
    @JsonCreator
    public static ProjectionType fromValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tipo de proyección no puede estar vacío");
        }
        for (ProjectionType pt : values()) {
            if (pt.code.equalsIgnoreCase(value.trim())) {
                return pt;
            }
        }
        throw new IllegalArgumentException("Tipo de proyección desconocido: " + value);
    }

    @Override
    public String toString() {
        return code;
    }

}