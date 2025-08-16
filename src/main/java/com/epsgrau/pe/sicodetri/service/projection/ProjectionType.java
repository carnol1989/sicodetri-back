package com.epsgrau.pe.sicodetri.service.projection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the different types of fixed asset projection generation.
 * Each enum value has:
 * <ul>
 *   <li>a code (used in HTTP query parameters or JSON payloads)</li>
 *   <li>a human-readable description (for logs or UI display)</li>
 * </ul>
 *
 * <b>Class</b>: {@link ProjectionType}<br/>
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
    TEMPORARY_ADDITIONS("TEMPORARY_ADDITION", "Adición temporal"),

    /**
     * Projection of permanent additions.
     */
    PERMANENT_ADDITIONS("PERMANENT_ADDITIONS", "Adición permanente");

    /**
     * Code to be used in the query-param or in JSON (e.g.: ?type=TEMPORARY_DEDUCTION).
     */
    private final String code;

    /**
     * Friendly description for logs or documentation.
     */
    private final String description;

    /**
     * Constructs a ProjectionType enum value with the given code and description.
     *
     * @param code        the string code for HTTP/JSON exchange
     * @param description a human-readable description
     */
    ProjectionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the code for this projection type.
     * Annotated with {@link JsonValue} so that Jackson serializes this value directly.
     *
     * @return the code string
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Returns the human-readable description of this projection type.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Creates a {@code ProjectionType} from the given code.
     * Comparison is case-insensitive and trims whitespace.
     *
     * @param value the code (e.g. "TEMPORARY_ADDITION")
     * @return the corresponding {@link ProjectionType}
     * @throws IllegalArgumentException if the code is null, empty, or does not match any type
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

    /**
     * Returns the code of this projection type.
     *
     * @return the code string
     */
    @Override
    public String toString() {
        return code;
    }

}