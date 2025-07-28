package com.epsgrau.pe.sicodetri.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload for returning detailed information about errors that occur during API execution.
 * This response object is used to convey a timestamp of occurrence, a human-readable
 * message, and any additional technical details useful for debugging.
 * <b>Class</b>: {@link ExceptionResponse}<br/>
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
@Schema(description = "Información detallada sobre errores ocurridos durante "
    + "la ejecución de la API.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    @Schema(description = "Fecha y hora en que ocurrió el error.", example = "2024-08-28T12:01:00")
    private LocalDateTime timestamp;

    @Schema(description = "Mensage del error.", example = "Error interno "
        + "del servidor.")
    private String message;

    @Schema(description = "Detalles técnicos del error.", example = "Mensaje: NullPointerException"
        + " en línea 42\nCausa: null")
    private String details;
}
