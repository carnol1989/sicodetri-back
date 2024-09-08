package com.epsgrau.pe.sicodetri.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class is custom exception response. <br/>
 * <b>Class</b>: {@link ExceptionResponse}<br/>
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
 *     <li>Ago 15, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Schema(description = "Información de la respuesta de error.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    @Schema(description = "Fecha y hora del error.")
    private LocalDateTime timestamp;

    @Schema(description = "Mensage del error.")
    private String message;

    @Schema(description = "Detalles técnicos del error.")
    private String details;

}
