package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class is a model for mapping response data. <br/>
 * <b>Class</b>: {@link ResponseData}<br/>
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
 *     <li>Ago 17, 2024 (JAR) Creation class.</li>
 *     </ul>
 * @version 1.0
 */
@Schema(description = "Información de la respuesta del API.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    @Schema(description = "Fecha y hora del error.")
    private LocalDateTime timestamp;

    @Schema(description = "Código de respuesta de la petición.", example = "00=OK, 01=ERROR")
    private String code;

    @Schema(description = "Mensaje de la petición.")
    private String message;

    @Schema(description = "Objeto que contiene la respuesta de la petición.")
    private Object response;

    @Schema(description = "Información de la respuesta de error.")
    private ExceptionResponse exception;

}
