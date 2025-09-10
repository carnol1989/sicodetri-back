package com.epsgrau.pe.sicodetri.util.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of fixed-asset projection types.
 * <p>
 * Each enum constant encapsulates:
 * </p>
 * <ul>
 *   <li>a stable <b>code</b> used for HTTP query parameters and JSON payloads, and</li>
 *   <li>a human-readable <b>description</b> for logs or UI.</li>
 * </ul>
 *
 * <p><b>Serialization</b>:</p>
 * <ul>
 *   <li>{@link #getCode()} is annotated with {@link com.fasterxml.jackson.annotation.JsonValue},
 *       so Jackson serializes a {@code ProjectionType} as its code (e.g., {@code "TEMPORARY_DEDUCTION"}).</li>
 *   <li>{@link #fromValue(String)} is annotated with {@link com.fasterxml.jackson.annotation.JsonCreator},
 *       allowing case-insensitive deserialization from the code string.</li>
 * </ul>
 *
 * <p><b>API contract</b>: the {@code code} values are part of the public API surface
 * (e.g., {@code ?type=TEMPORARY_DEDUCTION}). Changing them may break clients.</p>
 *
 * <b>Class</b>: {@link ProjectionType}<br/>
 * <b>Copyright</b>: &copy; 2024 EPS Grau. <br/>
 * <b>Company</b>: EPS Grau. <br/>
 *
 * @author EPS Grau
 *     <u>Service Provider</u>: Consultor TI <br/>
 *     <u>Developed by</u>:
 *     <ul>
 *       <li>Carlos Augusto Nole Machaca</li>
 *     </ul>
 *     <u>Changes</u>:
 *     <ul>
 *       <li>Jun 11, 2025 (JAR) Initial version.</li>
 *     </ul>
 * @version 1.1
 */
public enum  ProjectionType {

    /**
     * Projection of temporary deductions.
     * <p><b>Code:</b> {@code TEMPORARY_DEDUCTION}</p>
     */
    TEMPORARY_DEDUCTIONS("TEMPORARY_DEDUCTION", "Deducción temporal"),

    /**
     * Projection of temporary additions.
     * <p><b>Code:</b> {@code TEMPORARY_ADDITION}</p>
     */
    TEMPORARY_ADDITIONS("TEMPORARY_ADDITION", "Adición temporal"),

    /**
     * Projection of permanent additions.
     * <p><b>Code:</b> {@code PERMANENT_ADDITION}</p>
     */
    PERMANENT_ADDITIONS("PERMANENT_ADDITION", "Adición permanente");

    private final String code;

    private final String description;

    /**
     * Constructs a {@link ProjectionType}.
     *
     * @param code        stable code for HTTP/JSON exchange
     * @param description human-readable description
     */
    ProjectionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the stable code of this projection type.
     * <p>
     * Annotated with {@link com.fasterxml.jackson.annotation.JsonValue} so Jackson writes this value
     * directly when serializing the enum.
     * </p>
     *
     * @return the code string (never {@code null})
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * Returns the human-readable description of this projection type.
     *
     * @return the description string (never {@code null})
     */
    public String getDescription() {
        return description;
    }

    /**
     * Resolves a {@link ProjectionType} from the given code (case-insensitive, trimmed).
     * <p>
     * Intended for JSON deserialization and manual parsing of request parameters.
     * </p>
     *
     * @param value the code string (e.g., {@code "TEMPORARY_ADDITION"})
     * @return the matching {@link ProjectionType}
     * @throws IllegalArgumentException if {@code value} is {@code null}, blank,
     *                                  or does not match any known code
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
     * Returns the {@linkplain #getCode() code} of this enum.
     * <p>
     * Useful for logs and when a String form is needed that matches the serialized value.
     * </p>
     *
     * @return the code string
     */
    @Override
    public String toString() {
        return code;
    }

}