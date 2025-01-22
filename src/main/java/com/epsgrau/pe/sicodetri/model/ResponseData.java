package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@Schema(description = "Información de la respuesta del API.")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    @Schema(description = "Fecha y hora de la respuesta.", example = "2024-08-28T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de respuesta de la petición.", example = "00=OK, 01=ERROR")
    private String code;

    @Schema(description = "Mensaje de la petición.", example = "Operación realizada correctamente.")
    private String message;

    @Schema(description = "Objeto que contiene los datos de la respuesta.")
    private Object response;

    @Schema(description = "Detalles de error si la operación falla.")
    private ExceptionResponse exception;

}
