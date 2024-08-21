package com.epsgrau.pe.sicodetri.model;

import com.epsgrau.pe.sicodetri.exception.ExceptionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This class is a model for mapping response data. <br/>
 * <b>Class</b>: {@link InputData}<br/>
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
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {

    private LocalDateTime timestamp;
    private String code;
    private String message;
    private Object response;
    private ExceptionResponse exception;

}
